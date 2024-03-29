package pl.north93.northplatform.globalshops.server.impl.price;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.globalshops.controller.cfg.ItemPriceCfg;
import pl.north93.northplatform.globalshops.server.IPlayerContainer;
import pl.north93.northplatform.globalshops.server.domain.IPrice;
import pl.north93.northplatform.globalshops.server.domain.Item;
import pl.north93.northplatform.api.economy.IAccountAccessor;
import pl.north93.northplatform.api.economy.ICurrency;
import pl.north93.northplatform.api.economy.IEconomyManager;
import pl.north93.northplatform.api.economy.ITransaction;
import pl.north93.northplatform.api.global.network.players.Identity;

@Slf4j
public final class MoneyPrice implements IPrice
{
    private final IEconomyManager manager;
    private final ICurrency       currency;
    private final double          amount;

    public MoneyPrice(final IEconomyManager manager, final String currencyId, final double amount)
    {
        this.manager = manager;
        this.currency = manager.getCurrency(currencyId);
        this.amount = amount;
    }

    public MoneyPrice(final ItemPriceCfg cfg, final IEconomyManager manager)
    {
        this(manager, cfg.getCurrencyId(), cfg.getAmount());
    }

    public double getOriginalAmount()
    {
        return this.amount;
    }

    public double getDiscount(final IPlayerContainer container, final Item item)
    {
        return container.getShards(item) / 100D;
    }

    public double getAmount(final IPlayerContainer container, final Item item)
    {
        final double reductionPercent = this.getDiscount(container, item);
        return this.amount - (this.amount * reductionPercent);
    }

    @Override
    public boolean canBuy(final IPlayerContainer container, final Item item)
    {
        final Identity identity = container.getBukkitPlayer().getIdentity();

        final IAccountAccessor accessor = this.manager.getUnsafeAccessor(this.currency, identity);
        return accessor.has(this.getAmount(container, item));
    }

    @Override
    public boolean processBuy(final IPlayerContainer container, final Item item)
    {
        final Identity identity = container.getBukkitPlayer().getIdentity();
        try (final ITransaction t = this.manager.openTransaction(this.currency, identity))
        {
            final double amount = this.getAmount(container, item);
            if (! t.has(amount))
            {
                return false;
            }

            t.remove(amount);
            return true;
        }
        catch (final Exception e)
        {
            log.error("Transaction failed in processBuy", e);
            return false;
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("currency", this.currency).append("amount", this.amount).toString();
    }
}
