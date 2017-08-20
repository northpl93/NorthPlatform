package pl.arieals.globalshops.server.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.globalshops.server.BuyResult;
import pl.arieals.globalshops.server.IPlayerContainer;
import pl.arieals.globalshops.shared.Item;

public class ItemCheckBuyEvent extends PlayerEvent
{
    private static final HandlerList handlers = new HandlerList();
    private final IPlayerContainer playerContainer;
    private final Item item;
    private final int  level;
    private BuyResult result = BuyResult.CAN_BUY;

    public ItemCheckBuyEvent(final Player who, final IPlayerContainer playerContainer, final Item item, final int level)
    {
        super(who);
        this.playerContainer = playerContainer;
        this.item = item;
        this.level = level;
    }

    public IPlayerContainer getPlayerContainer()
    {
        return this.playerContainer;
    }

    public Item getItem()
    {
        return this.item;
    }

    public int getLevel()
    {
        return this.level;
    }

    public BuyResult getResult()
    {
        return this.result;
    }

    public void setResult(final BuyResult result)
    {
        this.result = result;
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("item", this.item).append("level", this.level).append("result", this.result).toString();
    }
}
