package pl.north93.northplatform.minigame.bedwars.event;

import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.minigame.bedwars.cfg.BwShopEntry;

/**
 * Event wywolywany przed zakupem itemu i
 * podczas sprawdzania czy gracz moze go kupic.
 */
public class ItemPreBuyEvent extends BedWarsPlayerArenaEvent
{
    private static final HandlerList handlers = new HandlerList();
    private final BwShopEntry shopEntry;
    private final ItemStack price;
    private final boolean isCheck;
    private BuyStatus buyStatus;

    public ItemPreBuyEvent(final LocalArena arena, final INorthPlayer player, final BwShopEntry shopEntry, final ItemStack price, final boolean isCheck)
    {
        super(arena, player);
        this.shopEntry = shopEntry;
        this.price = price;
        this.isCheck = isCheck;
        this.buyStatus = BuyStatus.CAN_BUY;
    }

    public BwShopEntry getShopEntry()
    {
        return this.shopEntry;
    }

    public ItemStack getPrice()
    {
        return this.price;
    }

    /**
     * Umozliwia sprawdzenie czy item jest kupowany, czy tylko
     * wykonywane jest sprawdzenie do wyrenderowania sklepu.
     *
     * @return true jesli to tylko sprawdzenie do renderowania sklepu
     *         false jesli gracz kupuje przedmiot
     */
    public boolean isCheck()
    {
        return this.isCheck;
    }

    public BuyStatus getBuyStatus()
    {
        return this.buyStatus;
    }

    public void setBuyStatus(final BuyStatus buyStatus)
    {
        this.buyStatus = buyStatus;
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

    public enum BuyStatus
    {
        CAN_BUY,
        ALREADY_HAVE,
        NOT_ENOUGH_CURRENCY;

        public boolean canBuy()
        {
            return this == CAN_BUY;
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("shopEntry", this.shopEntry).append("price", this.price).append("isCheck", this.isCheck).append("buyStatus", this.buyStatus).toString();
    }
}
