package pl.arieals.api.minigame.shared.impl.match;

import java.time.Instant;
import java.util.Collection;
import java.util.UUID;
import java.util.logging.Logger;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.types.ObjectId;

import pl.arieals.api.minigame.shared.api.GameIdentity;
import pl.arieals.api.minigame.shared.api.match.IMatch;
import pl.arieals.api.minigame.shared.api.match.IMatchAccess;
import pl.arieals.api.minigame.shared.api.match.IMatchManager;
import pl.arieals.api.minigame.shared.api.statistics.HolderIdentity;
import pl.arieals.api.minigame.shared.api.statistics.IStatisticHolder;
import pl.arieals.api.minigame.shared.api.statistics.IStatisticsManager;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.storage.StorageConnector;

public class MatchManagerImpl implements IMatchManager
{
    private final Logger                    logger;
    private final MongoCollection<Document> matchesCollection;
    @Inject
    private IStatisticsManager statisticsManager;

    @Bean
    private MatchManagerImpl(final Logger logger, final StorageConnector storage)
    {
        this.logger = logger;
        final MongoDatabase mainDatabase = storage.getMainDatabase();
        this.matchesCollection = mainDatabase.getCollection("matches", Document.class);
    }

    @Override
    public IMatchAccess createMatch(final UUID arenaId, final GameIdentity identity, final UUID serverId, final String mapId, final Collection<Identity> startParticipants)
    {
        final Instant startTime = Instant.now();

        final MatchData data = new MatchData(UUID.randomUUID(), arenaId, serverId, identity, mapId, startTime, startParticipants);

        final Document document = data.toDocument();
        this.matchesCollection.insertOne(document);

        return this.createMatchImpl(document);
    }

    @Override
    public IMatch getMatch(final ObjectId matchId)
    {
        final Document matchDocument = this.matchesCollection.find(new Document("_id", matchId)).limit(1).first();
        if (matchDocument == null)
        {
            return null; // ?
        }

        return this.createMatchImpl(matchDocument);
    }
    
    public void save(final MatchData matchData)
    {
        final Document criteria = new Document("_id", matchData.getMatchId());
        this.matchesCollection.findOneAndReplace(criteria, matchData.toDocument());
    }

    private MatchImpl createMatchImpl(final Document document)
    {
        final MatchData matchData = new MatchData(document);

        final HolderIdentity holderIdentity = new HolderIdentity("match", matchData.getMatchId());
        final IStatisticHolder matchStatistics = this.statisticsManager.getHolder(holderIdentity);

        return new MatchImpl(this, matchStatistics, matchData);
    }
}
