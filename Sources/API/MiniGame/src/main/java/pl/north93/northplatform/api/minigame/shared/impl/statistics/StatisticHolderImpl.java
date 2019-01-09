package pl.north93.northplatform.api.minigame.shared.impl.statistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.Document;

import pl.north93.northplatform.api.minigame.shared.api.statistics.HolderIdentity;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IRecord;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatistic;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticFilter;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticHolder;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticUnit;
import pl.north93.northplatform.api.minigame.shared.api.statistics.filter.BestRecordFilter;
import pl.north93.northplatform.api.minigame.shared.api.statistics.filter.LatestRecordFilter;

class StatisticHolderImpl implements IStatisticHolder
{
    private static final Document TIME_SORT = new Document("time", -1); // najnowsze na górze
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
    public <UNIT extends IStatisticUnit> CompletableFuture<IRecord<UNIT>> getBest(final IStatistic<UNIT> statistic, final IStatisticFilter... filters)
    {
        final List<IStatisticFilter> filtersList = Lists.newArrayList(filters);
        filtersList.add(new BestRecordFilter());

        return this.getRecord(statistic, filtersList);
    }

    @Override
    public <UNIT extends IStatisticUnit> CompletableFuture<IRecord<UNIT>> getLatest(final IStatistic<UNIT> statistic, final IStatisticFilter... filters)
    {
        final List<IStatisticFilter> filtersList = Lists.newArrayList(filters);
        filtersList.add(new LatestRecordFilter());

        return this.getRecord(statistic, filtersList);
    }

    private <UNIT extends IStatisticUnit> CompletableFuture<IRecord<UNIT>> getRecord(final IStatistic<UNIT> statistic, final Collection<IStatisticFilter> filters)
    {
        final MongoCollection<Document> collection = this.manager.getCollection();

        final Document query = this.composeConditions(statistic, filters);
        final Document sort = this.composeSort(statistic, filters);

        final CompletableFuture<IRecord<UNIT>> future = new CompletableFuture<>();
        this.manager.getApiCore().getPlatformConnector().runTaskAsynchronously(() ->
        {
            final Document result = collection.find(query).sort(sort).first();
            future.complete(this.manager.documentToUnit(statistic, result));
        });

        return future;
    }

    @Override
    public <UNIT extends IStatisticUnit> CompletableFuture<IRecord<UNIT>> record(final IStatistic<UNIT> statistic, final UNIT value)
    {
        final MongoCollection<Document> collection = this.manager.getCollection();
        final String id = statistic.getId();

        final Document find = this.composeConditions(statistic, new ArrayList<>(0));

        final Document setContent = new Document("owner", this.holder.asBson()).append("statId", id).append("time", System.currentTimeMillis());
        value.toDocument(setContent);

        final CompletableFuture<IRecord<UNIT>> future = new CompletableFuture<>();
        this.manager.getApiCore().getPlatformConnector().runTaskAsynchronously(() ->
        {
            final FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().sort(TIME_SORT).upsert(true);
            final Document previous = collection.findOneAndUpdate(find, new Document("$set", setContent), options);
            if (previous != null)
            {
                previous.remove("_id");
                collection.insertOne(previous);
            }

            future.complete(this.manager.documentToUnit(statistic, previous));
        });

        return future;
    }

    @Override
    public <UNIT extends IStatisticUnit> CompletableFuture<IRecord<UNIT>> increment(final IStatistic<UNIT> statistic, final UNIT value)
    {
        final MongoCollection<Document> collection = this.manager.getCollection();
        final Document insertDocument = new Document();

        final Document query = this.composeConditions(statistic, new ArrayList<>());

        final Document setContent = new Document("owner", this.holder.asBson()).append("statId", statistic.getId()).append("time", System.currentTimeMillis());
        final Document valueDocument = new Document();
        value.toDocument(valueDocument);
        insertDocument.put("$set", setContent);
        insertDocument.put("$inc", valueDocument);

        final CompletableFuture<IRecord<UNIT>> future = new CompletableFuture<>();
        this.manager.getApiCore().getPlatformConnector().runTaskAsynchronously(() ->
        {
            final FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().sort(TIME_SORT).upsert(true);
            final Document previous = collection.findOneAndUpdate(query, insertDocument, options);
            if (previous != null)
            {
                previous.remove("_id");
                collection.insertOne(previous);
            }

            future.complete(this.manager.documentToUnit(statistic, previous));
        });

        return future;
    }

    private Document composeConditions(final IStatistic<?> statistic, final Collection<IStatisticFilter> filters)
    {
        final Document query = this.manager.composeConditions(statistic, filters);
        query.append("owner", this.holder.asBson());

        return query;
    }

    private Document composeSort(final IStatistic<?> statistic, final Collection<IStatisticFilter> filters)
    {
        final Document sort = new Document();
        filters.forEach(filter -> filter.appendSort(statistic, sort));

        return sort;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("holder", this.holder).toString();
    }
}
