package pl.arieals.api.minigame.shared.impl.match;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bson.Document;

import pl.arieals.api.minigame.shared.api.GameIdentity;
import pl.north93.zgame.api.global.network.players.Identity;

/*default*/ class MatchData
{
    private UUID                 matchId;
    private UUID                 arenaId;
    private UUID                 serverId;
    private GameIdentity         game;
    private String               mapId;
    private Instant              startTime;
    private Collection<Identity> startParticipants;
    private Instant              endedAt;

    public MatchData(final UUID matchId, final UUID arenaId, final UUID serverId, final GameIdentity game, final String mapId, final Instant startTime, final Collection<Identity> startParticipants)
    {
        this.matchId = matchId;
        this.arenaId = arenaId;
        this.serverId = serverId;
        this.game = game;
        this.mapId = mapId;
        this.startTime = startTime;
        this.startParticipants = startParticipants;
        this.endedAt = Instant.ofEpochMilli(0);
    }

    public MatchData(final Document document)
    {
        this.matchId = document.get("_id", UUID.class);
        this.arenaId = document.get("arenaId", UUID.class);
        this.serverId = document.get("serverId", UUID.class);
        this.game = GameIdentity.create(document.get("game", Document.class));
        this.mapId = document.get("mapId", String.class);
        this.startTime = Instant.ofEpochMilli(document.getLong("startTime"));
        this.endedAt = Instant.ofEpochMilli(document.getLong("endedAt"));

        final List<Document> startParticipants = document.get("startParticipants", List.class);
        this.startParticipants = startParticipants.stream().map(Identity::new).collect(Collectors.toList());
    }

    public UUID getMatchId()
    {
        return this.matchId;
    }

    public UUID getArenaId()
    {
        return this.arenaId;
    }

    public UUID getServerId()
    {
        return this.serverId;
    }

    public GameIdentity getGame()
    {
        return this.game;
    }

    public Instant getStartTime()
    {
        return this.startTime;
    }

    public String getMapId()
    {
        return this.mapId;
    }

    public Collection<Identity> getStartParticipants()
    {
        return this.startParticipants;
    }

    public Instant getEndedAt()
    {
        return this.endedAt;
    }

    public void setEndedAt(final Instant endedAt)
    {
        this.endedAt = endedAt;
    }

    public Document toDocument()
    {
        final Document document = new Document();

        document.put("_id", this.matchId);
        document.put("arenaId", this.arenaId);
        document.put("serverId", this.serverId);
        document.put("game", this.game.toDocument());
        document.put("mapId", this.mapId);
        document.put("startTime", this.startTime.toEpochMilli());
        document.put("endedAt", this.endedAt.toEpochMilli());

        final List<Document> startParticipants = this.startParticipants.stream().map(Identity::toDocument).collect(Collectors.toList());
        document.put("startParticipants", startParticipants);

        return document;
    }
}
