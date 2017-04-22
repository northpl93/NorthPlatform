package pl.arieals.api.minigame.shared.api.arena.netevent;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.shared.api.GamePhase;

public class ArenaDataChanged implements IArenaNetEvent
{
    private UUID      arenaId;
    private GamePhase gamePhase;
    private Integer   playersCount;

    public ArenaDataChanged() // serialization
    {
    }

    public ArenaDataChanged(final UUID arenaId, final GamePhase gamePhase, final Integer playersCount)
    {
        this.arenaId = arenaId;
        this.gamePhase = gamePhase;
        this.playersCount = playersCount;
    }

    @Override
    public UUID getArenaId()
    {
        return this.arenaId;
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
    public ArenaEventType getEventType()
    {
        return ArenaEventType.DATA_CHANGED;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("arenaId", this.arenaId).append("gamePhase", this.gamePhase).append("playersCount", this.playersCount).toString();
    }
}
