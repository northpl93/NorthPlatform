package pl.north93.northplatform.api.minigame.shared.impl.statistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;

import org.bson.Document;
import org.bson.conversions.Bson;

import lombok.ToString;
import pl.north93.northplatform.api.minigame.shared.api.statistics.HolderIdentity;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IRecord;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatistic;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticFilter;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticHolder;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticUnit;
import pl.north93.northplatform.api.minigame.shared.api.statistics.filter.BestRecordFilter;
import pl.north93.northplatform.api.minigame.shared.api.statistics.filter.LatestRecordFilter;

@ToString(of = "holder")
class StatisticHolderImpl implements IStatisticHolder
{
    private static final Document TIME_SORT = new Document("time", -1); // newest on the top
    private final MongoCollection<Document> collection;
    private final StatisticsManagerImpl manager;
    private final HolderIdentity holder;

    public StatisticHolderImpl(final MongoCollection<Document> collection, final StatisticsManagerImpl manager, final HolderIdentity holder)
    {
        this.collection = collection;
        this.manager = manager;
        this.holder = holder;
    }

    @Override
    public HolderIdentity getIdentity()
    {
        return this.holder;
    }

    @Override
    public <T, UNIT extends IStatisticUnit<T>> CompletableFuture<IRecord<T, UNIT>> getBest(final IStatistic<T, UNIT> statistic, final IStatisticFilter... filters)
    {
        final List<IStatisticFilter> filtersList = Lists.newArrayList(filters);
        filtersList.add(new BestRecordFilter());

        return this.getRecord(statistic, filtersList);
    }

    @Override
    public <T, UNIT extends IStatisticUnit<T>> CompletableFuture<IRecord<T, UNIT>> getLatest(final IStatistic<T, UNIT> statistic, final IStatisticFilter... filters)
    {
        final List<IStatisticFilter> filtersList = Lists.newArrayList(filters);
        filtersList.add(new LatestRecordFilter());

        return this.getRecord(statistic, filtersList);
    }

    private <T, UNIT extends IStatisticUnit<T>> CompletableFuture<IRecord<T, UNIT>> getRecord(final IStatistic<T, UNIT> statistic, final Collection<IStatisticFilter> filters)
    {
        final Bson query = this.composeConditions(statistic, filters);
        final Bson sort = this.manager.composeSort(statistic, filters);

        return this.manager.createAndCompleteFutureAsync(() ->
        {
            final Document result = this.collection.find(query).sort(sort).first();
            return this.manager.documentToUnit(statistic, result);
        });
    }

    @Override
    public <T, UNIT extends IStatisticUnit<T>> CompletableFuture<IRecord<T, UNIT>> addRecord(final IStatistic<T, UNIT> statistic, final UNIT value)
    {
        final Document setContent = this.composeSetContent(statistic);
        value.toDocument(setContent);

        final Bson query = this.composeConditions(statistic, new ArrayList<>(0));
        return this.manager.createAndCompleteFutureAsync(() ->
        {
            final FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().sort(TIME_SORT).upsert(true);
            final Document previous = this.collection.findOneAndUpdate(query, new Document("$set", setContent), options);
            if (previous != null)
            {
                previous.remove("_id");
                this.collection.insertOne(previous);
            }

            return this.manager.documentToUnit(statistic, previous);
        });
    }

    @Override
    public <T, UNIT extends IStatisticUnit<T>> CompletableFuture<IRecord<T, UNIT>> incrementRecord(final IStatistic<T, UNIT> statistic, final UNIT value)
    {
        final Document setContent = this.composeSetContent(statistic);

        final Document valueDocument = new Document();
        value.toDocument(valueDocument);

        final Document insertDocument = new Document();
        insertDocument.put("$set", setContent);
        insertDocument.put("$inc", valueDocument);

        final Bson query = this.composeConditions(statistic, new ArrayList<>());
        return this.manager.createAndCompleteFutureAsync(() ->
        {
            final FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().sort(TIME_SORT).upsert(true);
            final Document previous = this.collection.findOneAndUpdate(query, insertDocument, options);
            if (previous != null)
            {
                previous.remove("_id");
                this.collection.insertOne(previous);
            }

            return this.manager.documentToUnit(statistic, previous);
        });
    }

    private <T, UNIT extends IStatisticUnit<T>> Document composeSetContent(final IStatistic<T, UNIT> statistic)
    {
        return new Document("owner", this.holder.asBson()).append("statId", statistic.getId()).append("time", System.currentTimeMillis());
    }

    private Bson composeConditions(final IStatistic<?, ?> statistic, final Collection<IStatisticFilter> filters)
    {
        final Collection<Bson> query = this.manager.composeConditions(statistic, filters);
        query.add(Filters.eq("owner", this.holder.asBson()));

        return Filters.and(query);
    }
}
