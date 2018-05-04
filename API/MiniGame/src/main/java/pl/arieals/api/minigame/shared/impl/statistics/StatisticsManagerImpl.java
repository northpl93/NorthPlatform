package pl.arieals.api.minigame.shared.impl.statistics;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.Document;

import pl.arieals.api.minigame.shared.api.statistics.HolderIdentity;
import pl.arieals.api.minigame.shared.api.statistics.IRanking;
import pl.arieals.api.minigame.shared.api.statistics.IRecord;
import pl.arieals.api.minigame.shared.api.statistics.IStatistic;
import pl.arieals.api.minigame.shared.api.statistics.IStatisticDbComposer;
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
    public <UNIT extends IStatisticUnit> CompletableFuture<IRanking> getRanking(final IStatistic<UNIT> statistic, final int size)
    {
        final MongoCollection<Document> collection = this.getCollection();
        final IStatisticDbComposer<UNIT> dbComposer = statistic.getDbComposer();

        final Document query = new Document("statId", statistic.getId());
        final FindIterable<Document> documents = dbComposer.bestRecordQuery(collection.find(query))
                                                           .limit(size);

        final CompletableFuture<IRanking> future = new CompletableFuture<>();
        this.apiCore.getPlatformConnector().runTaskAsynchronously(() ->
        {
            final Stream<Document> stream = StreamSupport.stream(documents.spliterator(), false);
            final List<IRecord<UNIT>> result = stream.map(document -> this.documentToUnit(statistic, document))
                                                     .collect(Collectors.toList());

            future.complete(new RankingImpl<>(size, result));
        });

        return future;
    }

    @Override
    public <UNIT extends IStatisticUnit> CompletableFuture<IRecord<UNIT>> getBestRecord(final IStatistic<UNIT> statistic)
    {
        final MongoCollection<Document> collection = this.getCollection();
        final IStatisticDbComposer<UNIT> dbComposer = statistic.getDbComposer();

        final Document query = new Document("statId", statistic.getId());
        final FindIterable<Document> documents = dbComposer.bestRecordQuery(collection.find(query))
                                                           .limit(1);

        final CompletableFuture<IRecord<UNIT>> future = new CompletableFuture<>();
        this.apiCore.getPlatformConnector().runTaskAsynchronously(() ->
        {
            final Document result = documents.limit(1).first();
            final IRecord<UNIT> resultRecord = this.documentToUnit(statistic, result);

            future.complete(resultRecord);
        });

        return future;
    }

    @Override
    public <UNIT extends IStatisticUnit> CompletableFuture<UNIT> getAverage(final IStatistic<UNIT> statistic)
    {
        final MongoCollection<Document> collection = this.getCollection();

        final Document group = new Document("_id", null).append("value", new Document("$avg", "$value"));
        final Document match = new Document("statId", statistic.getId());

        final List<Document> documents = Arrays.asList(new Document("$match", match), new Document("$group", group));

        final CompletableFuture<UNIT> future = new CompletableFuture<>();
        this.apiCore.getPlatformConnector().runTaskAsynchronously(() ->
        {
            final Document first = collection.aggregate(documents).first();

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

        final Document sort = new Document("$sort", new Document("value", 1));

        final Document group = new Document("_id", "$_id");
        group.put("value", new Document("$push", "$value"));

        final List<Serializable> multiply = Arrays.asList(percentile, new Document("$size", "$value")); // pozycja dokumentu w double
        final List<Object> arrayElemAt = Arrays.asList("$value", new Document("$floor", new Document("$multiply", multiply)));
        final Document project = new Document("_id", null);
        project.put("value", new Document("$arrayElemAt", arrayElemAt));

        final List<Document> documents = Arrays.asList(sort, new Document("$group", group), new Document("$project", project));

        final CompletableFuture<UNIT> future = new CompletableFuture<>();
        this.apiCore.getPlatformConnector().runTaskAsynchronously(() ->
        {
            final Document first = collection.aggregate(documents).first();

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
        final Instant recordedAt = Instant.ofEpochMilli(document.getLong("recordedAt"));

        return new RecordImpl<>(statistic, this.getHolder(holder), recordedAt, value);
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
