package pl.arieals.api.minigame.shared.api.arena.netevent;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.shared.api.GamePhase;

public class ArenaDataChangedNetEvent implements IArenaNetEvent
{
    private UUID      arenaId;
    private String    miniGameId;
    private String    worldId;
    private GamePhase gamePhase;
    private Integer   playersCount;

    public ArenaDataChangedNetEvent() // serialization
    {
    }

    public ArenaDataChangedNetEvent(final UUID arenaId, final String miniGameId, final String worldId, final GamePhase gamePhase, final Integer playersCount)
    {
        this.arenaId = arenaId;
        this.miniGameId = miniGameId;
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
    public String getMiniGameId()
    {
        return this.miniGameId;
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("arenaId", this.arenaId).append("miniGameId", this.miniGameId).append("worldId", this.worldId).append("gamePhase", this.gamePhase).append("playersCount", this.playersCount).toString();
    }
}
