package pl.north93.northplatform.api.economy.impl.server.event;

import org.bukkit.event.HandlerList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.player.event.NorthPlayerEvent;
import pl.north93.northplatform.api.economy.ICurrency;

/**
 * Event Bukkita wywoływany gdy zmienia się poziom waluty gracza na lokalnym serwerze.
 * Informacje z tego eventu należy traktować informacyjnie.
 *
 * UWAGA! Nie zostaną złapane zmiany waluty wykonane z zdalnych serwerów.
 */
public class PlayerCurrencyChangedEvent extends NorthPlayerEvent
{
    private static final HandlerList handlers = new HandlerList();
    private final        ICurrency   currency;
    private final        double      newAmount;

    public PlayerCurrencyChangedEvent(final INorthPlayer who, final ICurrency currency, final double newAmount)
    {
        super(who);
        this.currency = currency;
        this.newAmount = newAmount;
    }

    public ICurrency getCurrency()
    {
        return this.currency;
    }

    public double getNewAmount()
    {
        return this.newAmount;
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("currency", this.currency).append("newAmount", this.newAmount).toString();
    }
}
