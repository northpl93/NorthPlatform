package pl.north93.northplatform.api.minigame.shared.api.arena.netevent;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.minigame.shared.api.GameIdentity;
import pl.north93.northplatform.api.minigame.shared.api.GamePhase;

public class ArenaDataChangedNetEvent implements IArenaNetEvent
{
    private UUID         arenaId;
    private GameIdentity miniGame;
    private String       worldId;
    private GamePhase    gamePhase;
    private Integer      playersCount;

    public ArenaDataChangedNetEvent() // serialization
    {
    }

    public ArenaDataChangedNetEvent(final UUID arenaId, final GameIdentity miniGame, final String worldId, final GamePhase gamePhase, final Integer playersCount)
    {
        this.arenaId = arenaId;
        this.miniGame = miniGame;
        this.worldId = worldId;
        this.gamePhase = gamePhase;
        this.playersCount = playersCount;
    }

    @Override
    public UUID getArenaId()
    {
        return this.arenaId;
    }

    @Override
    public GameIdentity getMiniGame()
    {
        return this.miniGame;
    }

    public String getWorldId()
    {
        return this.worldId;
    }

    public GamePhase getGamePhase()
    {
        return this.gamePhase;
    }

    public Integer getPlayersCount()
    {
        return this.playersCount;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("arenaId", this.arenaId).append("miniGame", this.miniGame).append("worldId", this.worldId).append("gamePhase", this.gamePhase).append("playersCount", this.playersCount).toString();
    }
}
