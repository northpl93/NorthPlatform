package pl.arieals.api.minigame.shared.api.arena.netevent;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ArenaDeletedEvent implements IArenaNetEvent
{
    private UUID   arenaId;
    private UUID   serverId;
    private String miniGameId;

    public ArenaDeletedEvent()
    {
    }

    public ArenaDeletedEvent(final UUID arenaId, final UUID serverId, final String miniGameId)
    {
        this.arenaId = arenaId;
        this.serverId = serverId;
        this.miniGameId = miniGameId;
    }

    @Override
    public UUID getArenaId()
    {
        return this.arenaId;
    }

    public UUID getServerId()
    {
        return this.serverId;
    }

    @Override
    public String getMiniGameId()
    {
        return this.miniGameId;
    }

    @Override
    public ArenaEventType getEventType()
    {
        return ArenaEventType.DELETED;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("arenaId", this.arenaId).append("serverId", this.serverId).append("miniGameId", this.miniGameId).toString();
    }
}
