package pl.arieals.api.minigame.shared.api.arena;

import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.shared.api.GameIdentity;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.north93.zgame.api.global.metadata.MetaStore;
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
    private Boolean      isDynamic;
    private String       worldId;
    private GamePhase    gamePhase;
    private Integer      maxPlayers;
    @MsgPackCustomTemplate(HashSetTemplate.class)
    private Set<UUID>    players;
    private String       worldDisplayName;
    private MetaStore    metadata;

    public RemoteArena()
    {
    }

    public RemoteArena(final UUID arenaId, final UUID serverId, final GameIdentity miniGame, final Boolean isDynamic, final String worldId, final GamePhase gamePhase, final Integer maxPlayers, final Set<UUID> players)
    {
        this.arenaId = arenaId;
        this.serverId = serverId;
        this.miniGame = miniGame;
        this.isDynamic = isDynamic;
        this.worldId = worldId;
        this.gamePhase = gamePhase;
        this.maxPlayers = maxPlayers;
        this.players = players;
        this.metadata = new MetaStore();
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
    public boolean isDynamic()
    {
        return this.isDynamic;
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

    public String getWorldDisplayName()
    {
        return worldDisplayName;
    }
    
    public void setWorldDisplayName(String worldDisplayName)
    {
        this.worldDisplayName = worldDisplayName;
    }
    
    @Override
    public MetaStore getMetadata()
    {
        return metadata;
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
    public int getMaxPlayers()
    {
        return this.maxPlayers;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("arenaId", this.arenaId).append("serverId", this.serverId).append("miniGame", this.miniGame).append("isDynamic", this.isDynamic).append("worldId", this.worldId).append("gamePhase", this.gamePhase).append("maxPlayers", this.maxPlayers).append("players", this.players).toString();
    }
}
