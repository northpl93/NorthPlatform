package pl.arieals.api.minigame.server.lobby.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.shared.api.arena.netevent.ArenaEventType;
import pl.arieals.api.minigame.shared.api.arena.netevent.IArenaNetEvent;

public class IncomingArenaNetEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private final IArenaNetEvent arenaNetEvent;

    public IncomingArenaNetEvent(final IArenaNetEvent arenaNetEvent)
    {
        this.arenaNetEvent = arenaNetEvent;
    }

    public IArenaNetEvent getArenaNetEvent()
    {
        return this.arenaNetEvent;
    }

    public ArenaEventType getType()
    {
        return this.arenaNetEvent.getType();
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("arenaNetEvent", this.arenaNetEvent).toString();
    }
}
