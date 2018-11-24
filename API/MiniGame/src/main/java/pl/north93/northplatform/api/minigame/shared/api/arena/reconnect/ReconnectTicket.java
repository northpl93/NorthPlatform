package pl.north93.northplatform.api.minigame.shared.api.arena.reconnect;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.metadata.MetaKey;
import pl.north93.northplatform.api.global.metadata.MetaStore;

public class ReconnectTicket
{
    private static final MetaKey EXPIRATION = MetaKey.get("reconnectTicket_expiration");
    private static final MetaKey PLAYER_ID  = MetaKey.get("reconnectTicket_playerId");
    private static final MetaKey SERVER_ID  = MetaKey.get("reconnectTicket_serverId");
    private static final MetaKey ARENA_ID   = MetaKey.get("reconnectTicket_arenaId");
    private static final MetaKey MATCH_ID   = MetaKey.get("reconnectTicket_matchId");
    private Instant expiration;
    private UUID    playerId;
    private UUID    serverId;
    private UUID    arenaId;
    private UUID    matchId;

    public ReconnectTicket()
    {
    }

    public ReconnectTicket(final Instant expiration, final UUID playerId, final UUID serverId, final UUID arenaId, final UUID matchId)
    {
        this.expiration = expiration;
        this.playerId = playerId;
        this.serverId = serverId;
        this.arenaId = arenaId;
        this.matchId = matchId;
    }

    public ReconnectTicket(final MetaStore store)
    {
        this.expiration = store.getInstant(EXPIRATION);
        this.playerId = store.get(PLAYER_ID);
        this.serverId = store.get(SERVER_ID);
        this.arenaId = store.get(ARENA_ID);
        this.matchId = store.get(MATCH_ID);
    }

    public static boolean hasTicket(final MetaStore store)
    {
        return store.contains(EXPIRATION);
    }

    public static void removeTicket(final MetaStore store)
    {
        store.remove(EXPIRATION);
        store.remove(PLAYER_ID);
        store.remove(SERVER_ID);
        store.remove(ARENA_ID);
        store.remove(MATCH_ID);
    }

    public void setTicket(final MetaStore store)
    {
        store.setInstant(EXPIRATION, this.expiration);
        store.set(PLAYER_ID, this.playerId);
        store.set(SERVER_ID, this.serverId);
        store.set(ARENA_ID, this.arenaId);
        store.set(MATCH_ID, this.matchId);
    }

    public Instant getExpiration()
    {
        return this.expiration;
    }

    public UUID getPlayerId()
    {
        return this.playerId;
    }

    public UUID getServerId()
    {
        return this.serverId;
    }

    public UUID getArenaId()
    {
        return this.arenaId;
    }

    public UUID getMatchId()
    {
        return this.matchId;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || this.getClass() != o.getClass())
        {
            return false;
        }
        final ReconnectTicket that = (ReconnectTicket) o;
        return Objects.equals(this.expiration, that.expiration) &&
                       Objects.equals(this.playerId, that.playerId) &&
                       Objects.equals(this.serverId, that.serverId) &&
                       Objects.equals(this.arenaId, that.arenaId) &&
                       Objects.equals(this.matchId, that.matchId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.expiration, this.playerId, this.serverId, this.arenaId, this.matchId);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("expiration", this.expiration).append("playerId", this.playerId).append("serverId", this.serverId).append("arenaId", this.arenaId).append("matchId", this.matchId).toString();
    }
}
