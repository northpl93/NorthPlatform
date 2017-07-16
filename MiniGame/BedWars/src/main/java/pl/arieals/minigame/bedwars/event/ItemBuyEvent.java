package pl.arieals.minigame.bedwars.event;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.ArenaEvent;
import pl.arieals.minigame.bedwars.cfg.BwShopEntry;

/**
 * Event wywoływany gdy gracz kupuje przedmiot ze sklepu.
 * Wywołuje się przed dodaniem przedmiotów do ekwipunku i
 * przed pobraniem zapłaty i po sprawdzeniu czy gracz ma
 * wymaganą opłatę.
 * @see pl.arieals.minigame.bedwars.shop.ShopManager
 */
public class ItemBuyEvent extends ArenaEvent implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private final Player          player;
    private final BwShopEntry     shopEntry;
    private final List<ItemStack> items;
    private boolean cancelled;

    public ItemBuyEvent(final LocalArena arena, final Player player, final BwShopEntry shopEntry, final List<ItemStack> items)
    {
        super(arena);
        this.player = player;
        this.shopEntry = shopEntry;
        this.items = items;
    }

    /**
     * Zwraca gracza kupującego przedmiot.
     * @return gracz kupujący przedmiot.
     */
    public Player getPlayer()
    {
        return this.player;
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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("player", this.player).append("shopEntry", this.shopEntry).append("items", this.items).append("cancelled", this.cancelled).toString();
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
