package pl.arieals.api.minigame.shared.api.arena;

import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.shared.api.GameIdentity;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.north93.zgame.api.global.redis.messaging.annotations.MsgPackCustomTemplate;
import pl.north93.zgame.api.global.redis.messaging.templates.HashSetTemplate;

/**
 * Obiekt przedstawia arenę znajdującą się *gdzieś* w sieci.
 * Używany jest do gadania przez RPC.
 */
public class RemoteArena implements IArena
{
    private UUID         arenaId;
    private UUID         serverId;
    private GameIdentity miniGame;
    private String       worldId;
    private GamePhase    gamePhase;
    @MsgPackCustomTemplate(HashSetTemplate.class)
    private Set<UUID>    players;

    public RemoteArena()
    {
    }

    public RemoteArena(final UUID arenaId, final UUID serverId, final GameIdentity miniGame, final String worldId, final GamePhase gamePhase, final Set<UUID> players)
    {
        this.arenaId = arenaId;
        this.serverId = serverId;
        this.miniGame = miniGame;
        this.worldId = worldId;
        this.gamePhase = gamePhase;
        this.players = players;
    }

    @Override
    public UUID getId()
    {
        return this.arenaId;
    }

    @Override
    public UUID getServerId()
    {
        return this.serverId;
    }

    @Override
    public GameIdentity getMiniGame()
    {
        return this.miniGame;
    }

    @Override
    public String getWorldId()
    {
        return this.worldId;
    }

    public void setWorldId(final String worldId)
    {
        this.worldId = worldId;
    }

    @Override
    public GamePhase getGamePhase()
    {
        return this.gamePhase;
    }

    public void setGamePhase(final GamePhase gamePhase)
    {
        this.gamePhase = gamePhase;
    }

    @Override
    public Set<UUID> getPlayers()
    {
        return this.players;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("arenaId", this.arenaId).append("serverId", this.serverId).append("miniGame", this.miniGame).append("worldId", this.worldId).append("gamePhase", this.gamePhase).append("players", this.players).toString();
    }
}
