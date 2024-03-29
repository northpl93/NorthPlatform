package pl.north93.northplatform.minigame.bedwars.event;

import java.util.List;

import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.minigame.bedwars.cfg.BwShopEntry;
import pl.north93.northplatform.minigame.bedwars.shop.ShopManager;

/**
 * Event wywoływany gdy gracz kupuje przedmiot ze sklepu.
 * Wywołuje się przed dodaniem przedmiotów do ekwipunku i
 * przed pobraniem zapłaty i po sprawdzeniu czy gracz ma
 * wymaganą opłatę.
 * @see ShopManager
 */
public class ItemBuyEvent extends BedWarsPlayerArenaEvent
{
    private static final HandlerList handlers = new HandlerList();
    private final BwShopEntry shopEntry;
    private final List<ItemStack> items;

    public ItemBuyEvent(final LocalArena arena, final INorthPlayer player, final BwShopEntry shopEntry, final List<ItemStack> items)
    {
        super(arena, player);
        this.shopEntry = shopEntry;
        this.items = items;
    }

    /**
     * Zwraca kupowaną pozycję ze sklepu.
     * @return kupowana pozycja.
     */
    public BwShopEntry getShopEntry()
    {
        return this.shopEntry;
    }

    /**
     * Zwraca mutowalną listę przedmiotów która zostanie dodana do
     * ekwipunku gracza.
     * @return mutowalna lista przedmiotów.
     */
    public List<ItemStack> getItems()
    {
        return this.items;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("shopEntry", this.shopEntry).append("items", this.items).toString();
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
