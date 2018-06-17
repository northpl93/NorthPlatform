package pl.arieals.api.minigame.shared.impl.statistics;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.common.collect.Lists;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.Document;

import pl.arieals.api.minigame.shared.api.statistics.HolderIdentity;
import pl.arieals.api.minigame.shared.api.statistics.IRanking;
import pl.arieals.api.minigame.shared.api.statistics.IRecord;
import pl.arieals.api.minigame.shared.api.statistics.IStatistic;
import pl.arieals.api.minigame.shared.api.statistics.IStatisticFilter;
import pl.arieals.api.minigame.shared.api.statistics.IStatisticHolder;
import pl.arieals.api.minigame.shared.api.statistics.IStatisticUnit;
import pl.arieals.api.minigame.shared.api.statistics.IStatisticsManager;
import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.storage.StorageConnector;

public class StatisticsManagerImpl implements IStatisticsManager
{
    @Inject
    private ApiCore          apiCore;
    @Inject
    private StorageConnector storage;

    @Bean
    private StatisticsManagerImpl()
    {
    }

    @Override
    public IStatisticHolder getHolder(final HolderIdentity holder)
    {
        return new StatisticHolderImpl(this, holder);
    }

    @Override
    public <UNIT extends IStatisticUnit> CompletableFuture<IRanking> getRanking(final IStatistic<UNIT> statistic, final int size, final IStatisticFilter... filters)
    {
        final MongoCollection<Document> collection = this.getCollection();
        final Document sort = statistic.getDbComposer().bestRecordQuery();

        final List<Document> aggregation = this.composeStatisticsAggregation(statistic, Arrays.asList(filters));
        aggregation.add(new Document("$sort", sort)); // mongodb gubi kolejnosc po grupowaniu, wiec trzeba jeszcze raz posortowac
        aggregation.add(new Document("$limit", size)); // limitujemy rozmiar przed pobraniem do klienta

        final CompletableFuture<IRanking> future = new CompletableFuture<>();
        this.apiCore.getPlatformConnector().runTaskAsynchronously(() ->
        {
            final AggregateIterable<Document> documents = collection.aggregate(aggregation);
            final Stream<Document> stream = StreamSupport.stream(documents.spliterator(), false);

            final List<IRecord<UNIT>> result = stream.map(document -> this.documentToUnit(statistic, document))
                                                     .collect(Collectors.toList());

            future.complete(new RankingImpl<>(size, result));
        });

        return future;
    }

    @Override
    public <UNIT extends IStatisticUnit> CompletableFuture<IRecord<UNIT>> getRecord(final IStatistic<UNIT> statistic, final IStatisticFilter[] filters)
    {
        final MongoCollection<Document> collection = this.getCollection();

        final Document query = new Document("statId", statistic.getId());
        final Document sort = new Document();
        Arrays.stream(filters).forEach(filter -> filter.appendSort(statistic, sort));

        final CompletableFuture<IRecord<UNIT>> future = new CompletableFuture<>();
        this.apiCore.getPlatformConnector().runTaskAsynchronously(() ->
        {
            final Document result = collection.find(query).sort(sort).first();
            final IRecord<UNIT> resultRecord = this.documentToUnit(statistic, result);

            future.complete(resultRecord);
        });

        return future;
    }

    @Override
    public <UNIT extends IStatisticUnit> CompletableFuture<UNIT> getAverage(final IStatistic<UNIT> statistic)
    {
        final MongoCollection<Document> collection = this.getCollection();

        final List<Document> aggregation = this.composeStatisticsAggregation(statistic, new ArrayList<>());

        final Document group = new Document("_id", null).append("value", new Document("$avg", "$value"));
        aggregation.add(new Document("$group", group));

        final CompletableFuture<UNIT> future = new CompletableFuture<>();
        this.apiCore.getPlatformConnector().runTaskAsynchronously(() ->
        {
            final Document first = collection.aggregate(aggregation).first();

            final UNIT value;
            if (first == null)
            {
                value = statistic.getDbComposer().readValue(new Document("value", 0));
            }
            else
            {
                value = statistic.getDbComposer().readValue(first);
            }

            future.complete(value);
        });

        return future;
    }

    @Override
    public <UNIT extends IStatisticUnit> CompletableFuture<UNIT> getPercentile(final IStatistic<UNIT> statistic, final double percentile)
    {
        final MongoCollection<Document> collection = this.getCollection();

        //        {'$sort': {'value': 1}},
        //        {'$group': {'_id': '$_id', 'value': {'$push': '$value'}}},
        //        {'$project': {
        //            '_id': 1,
        //            'value': {'$arrayElemAt': ['$value', {'$floor': {'$multiply': [PERCENTILE, {'$size': '$value'}]}}]}
        //        }}

        final List<Document> aggregation = this.composeStatisticsAggregation(statistic, new ArrayList<>());

        // sortujemy
        aggregation.add(new Document("$sort", new Document("value", 1)));

        final Document group = new Document("_id", "$_id");
        group.put("value", new Document("$push", "$value"));
        aggregation.add(new Document("$group", group)); // sumujemy

        final List<Serializable> multiply = Arrays.asList(percentile, new Document("$size", "$value")); // pozycja dokumentu w double
        final List<Object> arrayElemAt = Arrays.asList("$value", new Document("$floor", new Document("$multiply", multiply)));
        final Document project = new Document("_id", null);
        project.put("value", new Document("$arrayElemAt", arrayElemAt));
        aggregation.add(new Document("$project", project)); // finalny projekt

        final CompletableFuture<UNIT> future = new CompletableFuture<>();
        this.apiCore.getPlatformConnector().runTaskAsynchronously(() ->
        {
            final Document first = collection.aggregate(aggregation).first();

            final UNIT value;
            if (first == null)
            {
                value = statistic.getDbComposer().readValue(new Document("value", 0));
            }
            else
            {
                value = statistic.getDbComposer().readValue(first);
            }

            future.complete(value);
        });

        return future;
    }

    @Override
    public <UNIT extends IStatisticUnit> CompletableFuture<UNIT> getMedian(final IStatistic<UNIT> statistic)
    {
        return this.getPercentile(statistic, 0.5);
    }

    /*default*/ <UNIT extends IStatisticUnit> IRecord<UNIT> documentToUnit(final IStatistic<UNIT> statistic, final Document document)
    {
        if (document == null)
        {
            return null;
        }

        final Document ownerDocument = document.get("owner", Document.class);
        final HolderIdentity holder = new HolderIdentity(ownerDocument.getString("type"), ownerDocument.get("uuid", UUID.class));

        final UNIT value = statistic.getDbComposer().readValue(document);
        final Instant recordedAt = Instant.ofEpochMilli(document.getLong("time"));

        return new RecordImpl<>(statistic, this.getHolder(holder), recordedAt, value);
    }

    /*default*/ List<Document> composeStatisticsAggregation(final IStatistic<?> statistic, final Collection<IStatisticFilter> filters)
    {
        final Document query = this.composeConditions(statistic, filters);

        // sortujemy wedlug kolejnosci uznawanej przez ta statystyke
        final Document sort = statistic.getDbComposer().bestRecordQuery();

        // wybieramy najlepsze wartosci
        final Document group = new Document("_id", "$owner");
        group.put("value", new Document("$first", "$value"));
        group.put("time", new Document("$first", "$time"));

        // remapujemy pola
        final Document project = new Document("owner", "$_id");
        project.put("statId", statistic.getId());
        project.put("value", 1);
        project.put("time", 1);

        return Lists.newArrayList(new Document("$match", query), new Document("$sort", sort), new Document("$group", group), new Document("$project", project));
    }

    /*default*/ Document composeConditions(final IStatistic<?> statistic, final Collection<IStatisticFilter> filters)
    {
        final Document query = new Document("statId", statistic.getId());
        filters.forEach(filter -> filter.appendConditions(statistic, query));

        return query;
    }

    /*default*/ ApiCore getApiCore()
    {
        return this.apiCore;
    }

    /*default*/ MongoCollection<Document> getCollection()
    {
        return this.storage.getMainDatabase().getCollection("statistics");
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
