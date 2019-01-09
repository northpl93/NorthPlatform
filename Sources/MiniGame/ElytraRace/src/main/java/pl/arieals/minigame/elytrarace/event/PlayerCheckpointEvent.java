package pl.arieals.minigame.elytrarace.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.minigame.elytrarace.cfg.Checkpoint;

public class PlayerCheckpointEvent extends PlayerEvent implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private final Checkpoint checkpoint;
    private boolean cancelled;

    public PlayerCheckpointEvent(final Player who, final Checkpoint checkpoint)
    {
        super(who);
        this.checkpoint = checkpoint;
    }

    public Checkpoint getCheckpoint()
    {
        return this.checkpoint;
    }

    @Override
    public boolean isCancelled()
    {
        return this.cancelled;
    }

    @Override
    public void setCancelled(final boolean cancelled)
    {
        this.cancelled = cancelled;
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("checkpoint", this.checkpoint).append("cancelled", this.cancelled).append("player", this.player).toString();
    }
}
