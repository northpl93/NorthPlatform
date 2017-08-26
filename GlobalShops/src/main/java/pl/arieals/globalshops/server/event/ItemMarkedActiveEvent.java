package pl.arieals.globalshops.server.event;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.globalshops.server.IPlayerContainer;
import pl.arieals.globalshops.shared.Item;
import pl.arieals.globalshops.shared.ItemsGroup;

/**
 * Event wywoływany gdy gracz wybiera item jako aktywny w grupie
 * {@link pl.arieals.globalshops.shared.GroupType#SINGLE_PICK}
 */
public class ItemMarkedActiveEvent extends PlayerEvent
{
    private static final HandlerList handlers = new HandlerList();
    private final IPlayerContainer container;
    private final ItemsGroup group;
    private final Item item;

    public ItemMarkedActiveEvent(final Player who, final IPlayerContainer container, final ItemsGroup group, final Item item)
    {
        super(who);
        this.container = container;
        this.group = group;
        this.item = item;
    }

    /**
     * Zwraca kontener reprezentujacy gracza.
     *
     * @return kontener reprezentujacy gracza.
     */
    public IPlayerContainer getContainer()
    {
        return this.container;
    }

    /**
     * Grupa w ktorej item zostal wybrany jako aktywny.
     * @return grupa w ktorej gracz dokonal wyboru.
     */
    public ItemsGroup getGroup()
    {
        return this.group;
    }

    /**
     * Przedmiot wybrany jako aktywny w grupie.
     * Moze byc null jesli gracz wybral opcje "Domyslny".
     *
     * @return przedmiot wybrany jako aktywny.
     */
    public @Nullable Item getItem()
    {
        return this.item;
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("group", this.group).append("item", this.item).toString();
    }
}
