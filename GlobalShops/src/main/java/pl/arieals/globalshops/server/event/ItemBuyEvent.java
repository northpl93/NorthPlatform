package pl.arieals.globalshops.server.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.globalshops.server.domain.Item;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.bukkit.player.event.NorthPlayerEvent;

public class ItemBuyEvent extends NorthPlayerEvent implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private final Item    item;
    private final int     level;
    private       boolean cancelled;

    public ItemBuyEvent(final INorthPlayer who, final Item item, final int level)
    {
        super(who);
        this.item = item;
        this.level = level;
    }

    public Item getItem()
    {
        return this.item;
    }

    public int getLevel()
    {
        return this.level;
    }

    @Override
    public boolean isCancelled()
    {
        return this.cancelled;
    }

    @Override
    public void setCancelled(final boolean b)
    {
        this.cancelled = b;
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("item", this.item).append("level", this.level).append("cancelled", this.cancelled).toString();
    }
}
