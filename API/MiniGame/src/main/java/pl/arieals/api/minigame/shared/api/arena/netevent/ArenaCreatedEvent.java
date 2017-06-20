package pl.arieals.api.minigame.shared.api.arena.netevent;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ArenaCreatedEvent implements IArenaNetEvent
{
    private UUID   uuid;
    private String miniGameId;

    public ArenaCreatedEvent()
    {
    }

    public ArenaCreatedEvent(final UUID uuid, final String miniGameId)
    {
        this.uuid = uuid;
        this.miniGameId = miniGameId;
    }

    @Override
    public UUID getArenaId()
    {
        return this.uuid;
    }

    @Override
    public String getMiniGameId()
    {
        return this.miniGameId;
    }

    @Override
    public ArenaEventType getEventType()
    {
        return ArenaEventType.CREATED;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("uuid", this.uuid).append("miniGameId", this.miniGameId).toString();
    }
}
