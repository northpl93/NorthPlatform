package pl.north93.northplatform.api.minigame.shared.impl.statistics;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.Document;
import org.bson.conversions.Bson;

import pl.north93.northplatform.api.global.HostConnector;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.storage.StorageConnector;
import pl.north93.northplatform.api.minigame.shared.api.statistics.HolderIdentity;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IRanking;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IRecord;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatistic;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticFilter;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticHolder;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticUnit;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticsManager;

public class StatisticsManagerImpl implements IStatisticsManager
{
    @Inject
    private HostConnector hostConnector;
    private final MongoCollection<Document> collection;

    @Bean
    private StatisticsManagerImpl(final StorageConnector storage)
    {
        this.collection = storage.getMainDatabase().getCollection("statistics");
    }

    @Override
    public IStatisticHolder getHolder(final HolderIdentity holder)
    {
        return new StatisticHolderImpl(this.collection, this, holder);
    }

    @Override
    public <T, UNIT extends IStatisticUnit<T>> CompletableFuture<IRanking<T, UNIT>> getRanking(final IStatistic<T, UNIT> statistic, final int size, final IStatisticFilter[] filters)
    {
        final Bson sort = this.composeSort(statistic, Arrays.asList(filters));

        final List<Bson> aggregation = this.composeStatisticsAggregation(statistic, Arrays.asList(filters));
        aggregation.add(Aggregates.sort(sort)); // mongodb loses order after grouping, so we'll sort again
        aggregation.add(Aggregates.limit(size)); // limit it before moving to the client

        return this.createAndCompleteFutureAsync(() ->
        {
            final AggregateIterable<Document> documents = this.collection.aggregate(aggregation);
            final Stream<Document> stream = StreamSupport.stream(documents.spliterator(), false);

            final List<IRecord<T, UNIT>> result = stream.map(document -> this.documentToUnit(statistic, document))
                                                        .collect(Collectors.toList());

            return new RankingImpl<>(size, result);
        });
    }

    @Override
    public <T, UNIT extends IStatisticUnit<T>> CompletableFuture<IRecord<T, UNIT>> getRecord(final IStatistic<T, UNIT> statistic, final IStatisticFilter[] filters)
    {
        final Bson query = Filters.eq("statId", statistic.getId());
        final Bson sort = this.composeSort(statistic, Arrays.asList(filters));

        return this.createAndCompleteFutureAsync(() ->
        {
            final Document result = this.collection.find(query).sort(sort).first();
            return this.documentToUnit(statistic, result);
        });
    }

    @Override
    public <T, UNIT extends IStatisticUnit<T>> CompletableFuture<UNIT> getAverage(final IStatistic<T, UNIT> statistic)
    {
        final List<Bson> aggregation = this.composeStatisticsAggregation(statistic, new ArrayList<>());
        aggregation.add(Aggregates.group(null, Accumulators.avg("value", "$value")));

        return this.createAndCompleteFutureAsync(() -> this.aggregateAndReadValue(statistic, aggregation));
    }

    @Override
    public <T, UNIT extends IStatisticUnit<T>> CompletableFuture<UNIT> getPercentile(final IStatistic<T, UNIT> statistic, final double percentile)
    {
        //        {'$sort': {'value': 1}},
        //        {'$group': {'_id': '$_id', 'value': {'$push': '$value'}}},
        //        {'$project': {
        //            '_id': 1,
        //            'value': {'$arrayElemAt': ['$value', {'$floor': {'$multiply': [PERCENTILE, {'$size': '$value'}]}}]}
        //        }}

        final List<Bson> aggregation = this.composeStatisticsAggregation(statistic, new ArrayList<>());

        // sorting
        aggregation.add(Aggregates.sort(Sorts.ascending("value")));

        // aggregation
        aggregation.add(Aggregates.group("$_id", Accumulators.push("value", "$value")));

        final List<Serializable> multiply = Arrays.asList(percentile, new Document("$size", "$value")); // pozycja dokumentu w double
        final List<Object> arrayElemAt = Arrays.asList("$value", new Document("$floor", new Document("$multiply", multiply)));
        aggregation.add(Aggregates.project(Projections.fields(
                Projections.excludeId(),
                Projections.computed("value", new Document("$arrayElemAt", arrayElemAt)))
        )); // final project

        return this.createAndCompleteFutureAsync(() -> this.aggregateAndReadValue(statistic, aggregation));
    }

    @Override
    public <T, UNIT extends IStatisticUnit<T>> CompletableFuture<UNIT> getMedian(final IStatistic<T, UNIT> statistic)
    {
        return this.getPercentile(statistic, 0.5);
    }

    /*default*/ <T, UNIT extends IStatisticUnit<T>> UNIT aggregateAndReadValue(final IStatistic<T, UNIT> statistic, final List<Bson> aggregation)
    {
        final Document resultDocument = this.collection.aggregate(aggregation).first();
        final Document nonNullResultDocument = Objects.requireNonNullElseGet(resultDocument, () -> new Document("value", 0));

        return statistic.getDbComposer().readValue(nonNullResultDocument);
    }

    /*default*/ <T, UNIT extends IStatisticUnit<T>> IRecord<T, UNIT> documentToUnit(final IStatistic<T, UNIT> statistic, final Document document)
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

    private List<Bson> composeStatisticsAggregation(final IStatistic<?, ?> statistic, final Collection<IStatisticFilter> filters)
    {
        final Bson query = Filters.and(this.composeConditions(statistic, filters));

        // sort according to order provided by our filters
        final Bson sort = Aggregates.sort(this.composeSort(statistic, filters));

        // choose "the best" values
        final Bson group = Aggregates.group("$owner",
                Accumulators.first("value", "$value"),
                Accumulators.first("time", "$time"));

        // convert it into standard form used in this code
        final Bson project = Aggregates.project(Projections.fields(
                Projections.computed("owner", "$_id"),
                Projections.computed("statId", statistic.getId()),
                Projections.include("value", "time")));

        return Lists.newArrayList(Aggregates.match(query), sort, group, project);
    }

    /*default*/ Collection<Bson> composeConditions(final IStatistic<?, ?> statistic, final Collection<IStatisticFilter> filters)
    {
        final Collection<Bson> conditions = new ArrayList<>();
        conditions.add(Filters.eq("statId", statistic.getId()));

        for (final IStatisticFilter filter : filters)
        {
            final Bson filterCondition = filter.getCondition(statistic);
            if (filterCondition != null)
            {
                conditions.add(filterCondition);
            }
        }

        return conditions;
    }

    /*default*/ Bson composeSort(final IStatistic<?, ?> statistic, final Collection<IStatisticFilter> filters)
    {
        Preconditions.checkArgument(! filters.isEmpty(), "You must specify at least one filter");
        return Sorts.orderBy(filters.stream().map(filter -> filter.getSort(statistic)).collect(Collectors.toList()));
    }

    /*default*/ <T> CompletableFuture<T> createAndCompleteFutureAsync(final Supplier<T> supplier)
    {
        final CompletableFuture<T> future = new CompletableFuture<>();
        this.hostConnector.runTaskAsynchronously(() -> future.complete(supplier.get()));

        return future;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
