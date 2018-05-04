package pl.arieals.api.minigame.shared.impl.statistics;

import java.util.concurrent.CompletableFuture;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.Document;

import pl.arieals.api.minigame.shared.api.statistics.HolderIdentity;
import pl.arieals.api.minigame.shared.api.statistics.IRecord;
import pl.arieals.api.minigame.shared.api.statistics.IStatistic;
import pl.arieals.api.minigame.shared.api.statistics.IStatisticHolder;
import pl.arieals.api.minigame.shared.api.statistics.IStatisticUnit;

class StatisticHolderImpl implements IStatisticHolder
{
    private final StatisticsManagerImpl manager;
    private final HolderIdentity        holder;

    public StatisticHolderImpl(final StatisticsManagerImpl manager, final HolderIdentity holder)
    {
        this.manager = manager;
        this.holder = holder;
    }

    @Override
    public HolderIdentity getIdentity()
    {
        return this.holder;
    }

    @Override
    public <UNIT extends IStatisticUnit> CompletableFuture<IRecord<UNIT>> getValue(final IStatistic<UNIT> statistic)
    {
        final MongoCollection<Document> collection = this.manager.getCollection();
        final Document query = new Document("owner", this.holder.asBson()).append("statId", statistic.getId());

        final CompletableFuture<IRecord<UNIT>> future = new CompletableFuture<>();
        this.manager.getApiCore().getPlatformConnector().runTaskAsynchronously(() ->
        {
            final Document result = collection.find(query).limit(1).first();
            future.complete(this.manager.documentToUnit(statistic, result));
        });

        return future;
    }

    @Override
    public <UNIT extends IStatisticUnit> CompletableFuture<IRecord<UNIT>> record(final IStatistic<UNIT> statistic, final UNIT value, final boolean onlyWhenBetter)
    {
        final MongoCollection<Document> collection = this.manager.getCollection();
        final String id = statistic.getId();

        final Document insertDocument = new Document();
        final Document setContent = new Document("owner", this.holder.asBson()).append("statId", id).append("recordedAt", System.currentTimeMillis());
        if (onlyWhenBetter)
        {
            statistic.getDbComposer().insertOnlyWhenBetter(insertDocument, value);
        }
        else
        {
            value.toDocument(setContent);
        }
        insertDocument.put("$set", setContent);

        final Document find = new Document("statId", id);
        find.put("owner", this.holder.asBson());

        final CompletableFuture<IRecord<UNIT>> future = new CompletableFuture<>();
        this.manager.getApiCore().getPlatformConnector().runTaskAsynchronously(() ->
        {
            final Document previous = collection.findOneAndUpdate(find, insertDocument, new FindOneAndUpdateOptions().upsert(true));
            future.complete(this.manager.documentToUnit(statistic, previous));
        });

        return future;
    }

    @Override
    public <UNIT extends IStatisticUnit> CompletableFuture<IRecord<UNIT>> increment(final IStatistic<UNIT> statistic, final UNIT value)
    {
        final MongoCollection<Document> collection = this.manager.getCollection();
        final Document insertDocument = new Document();

        final Document find = new Document("statId", statistic.getId());
        find.put("owner", this.holder.asBson());

        final Document setContent = new Document("owner", this.holder.asBson()).append("statId", statistic.getId()).append("recordedAt", System.currentTimeMillis());
        final Document valueDocument = new Document();
        value.toDocument(valueDocument);
        insertDocument.put("$set", setContent);
        insertDocument.put("$inc", valueDocument);

        final CompletableFuture<IRecord<UNIT>> future = new CompletableFuture<>();
        this.manager.getApiCore().getPlatformConnector().runTaskAsynchronously(() ->
        {
            final Document previous = collection.findOneAndUpdate(find, insertDocument, new FindOneAndUpdateOptions().upsert(true));
            future.complete(this.manager.documentToUnit(statistic, previous));
        });

        return future;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("holder", this.holder).toString();
    }
}
