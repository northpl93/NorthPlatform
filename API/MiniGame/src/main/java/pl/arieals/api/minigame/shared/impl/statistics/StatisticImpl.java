package pl.arieals.api.minigame.shared.impl.statistics;

import static com.mongodb.client.model.Filters.eq;


import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.UpdateOptions;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.Document;

import pl.arieals.api.minigame.shared.api.statistics.IRecord;
import pl.arieals.api.minigame.shared.api.statistics.IRecordResult;
import pl.arieals.api.minigame.shared.api.statistics.IStatistic;
import pl.arieals.api.minigame.shared.api.statistics.IStatisticEncoder;

public class StatisticImpl<E extends IStatisticEncoder> implements IStatistic<E>
{
    private final StatisticsManagerImpl manager;
    private final String  key;
    private final boolean isReversed;

    public StatisticImpl(final StatisticsManagerImpl manager, final String key, final boolean isReversed)
    {
        this.manager = manager;
        this.key = key;
        this.isReversed = isReversed;
    }

    @Override
    public String getKey()
    {
        return this.key;
    }

    @Override
    public boolean isReverseOrder()
    {
        return this.isReversed; // jak true to mniej=lepiej
    }

    @Override
    public CompletableFuture<IRecord> getGlobalRecord()
    {
        final CompletableFuture<IRecord> future = new CompletableFuture<>();
        this.manager.getApiCore().getPlatformConnector().runTaskAsynchronously(() ->
        {
            final MongoCollection<Document> globalRecords = this.manager.getStorage().getMainDatabase().getCollection("globalStats");
            final Document record = globalRecords.find(new Document("statId", this.key)).limit(1).first();
            future.complete(Optional.ofNullable(record).map(RecordImpl::new).orElse(null));
        });
        return future;
    }

    @Override
    public CompletableFuture<Long> getAverageValue()
    {
        final CompletableFuture<Long> future = new CompletableFuture<>();
        this.manager.getApiCore().getPlatformConnector().runTaskAsynchronously(() ->
        {
            final MongoCollection<Document> personalRecords = this.manager.getStorage().getMainDatabase().getCollection("personalStats");
            final Document group = new Document("_id", null);
            group.put("avg", new Document("$avg", "$value"));
            final Document result = personalRecords.aggregate(Collections.singletonList(new Document("$group", group))).first();
            future.complete(result.getDouble("avg").longValue());
        });
        return future;
    }

    @Override
    public CompletableFuture<IRecordResult> record(final UUID playerId, final E value)
    {
        final CompletableFuture<IRecordResult> future = new CompletableFuture<>();
        this.manager.getApiCore().getPlatformConnector().runTaskAsynchronously(() ->
        {
            future.complete(this.asyncRecord(playerId, value));
        });
        return future;
    }

    private IRecordResult asyncRecord(final UUID playerId, final E value)
    {
        final MongoCollection<Document> personalRecords = this.manager.getStorage().getMainDatabase().getCollection("personalStats");
        final MongoCollection<Document> globalRecords = this.manager.getStorage().getMainDatabase().getCollection("globalStats");
        final long newValue = value.get();

        final RecordImpl currentPersonal = new RecordImpl(playerId, newValue, System.currentTimeMillis());
        final RecordImpl previousPersonal;
        {
            final Document newDocument = new Document();
            newDocument.put(this.isReversed ? "$min" : "$max", new Document("value", newValue));
            newDocument.put("$set", new Document("ownerId", playerId).append("statId", this.key).append("recordedAt", currentPersonal.recordedAt()));

            final Document find = new Document("statId", this.key);
            find.put("ownerId", playerId);

            final Document prevPersonalDoc = personalRecords.findOneAndUpdate(find, newDocument, new FindOneAndUpdateOptions().upsert(true));
            previousPersonal = Optional.ofNullable(prevPersonalDoc).map(RecordImpl::new).orElse(null);
        }

        final RecordImpl previousGlobal;
        {
            // pobieramy poprzedni rekord globalny
            final Document lastGlobal = globalRecords.find(eq("statId", this.key)).limit(1).first();
            previousGlobal = Optional.ofNullable(lastGlobal).map(RecordImpl::new).orElse(null);

            if (previousGlobal == null || this.compare(previousGlobal, currentPersonal))
            {
                // jesli zostal pobity rekord globalny to wysylamy jego aktualizacje
                // do bazy danych
                globalRecords.updateOne(eq("statId", this.key), new Document("$set", currentPersonal.toDocument()), new UpdateOptions().upsert(true));
            }
        }

        return new RecordResultImpl(currentPersonal, previousPersonal, previousGlobal, this.isReversed);
    }

    private boolean compare(final IRecord first, final IRecord second)
    {
        return this.isReversed ? first.value() > second.value() : second.value() > first.value();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("manager", this.manager).append("key", this.key).append("isReversed", this.isReversed).toString();
    }
}
