package pl.north93.northplatform.api.minigame.shared.api.arena;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import lombok.NoArgsConstructor;
import lombok.ToString;
import pl.north93.northplatform.api.global.metadata.MetaStore;
import pl.north93.northplatform.api.minigame.shared.api.GameIdentity;
import pl.north93.northplatform.api.minigame.shared.api.GamePhase;
import pl.north93.serializer.platform.annotations.NorthField;

/**
 * Obiekt przedstawia arenę znajdującą się *gdzieś* w sieci.
 * Używany jest do gadania przez RPC.
 */
@ToString
@NoArgsConstructor
public class RemoteArena implements IArena
{
    private UUID arenaId;
    private UUID serverId;
    private GameIdentity miniGame;
    private Boolean isDynamic;
    private GamePhase gamePhase;
    private Integer maxPlayers;
    @NorthField(type = HashSet.class)
    private Set<UUID> players;
    private MetaStore metadata;

    public RemoteArena(final UUID arenaId, final UUID serverId, final GameIdentity miniGame, final Boolean isDynamic, final GamePhase gamePhase, final Integer maxPlayers, final Set<UUID> players)
    {
        this.arenaId = arenaId;
        this.serverId = serverId;
        this.miniGame = miniGame;
        this.isDynamic = isDynamic;
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
    public MetaStore getMetaStore()
    {
        return this.metadata;
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
}
