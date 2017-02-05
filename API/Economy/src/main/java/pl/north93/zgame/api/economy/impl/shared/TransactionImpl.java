package pl.north93.zgame.api.economy.impl.shared;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.economy.ICurrency;
import pl.north93.zgame.api.economy.ITransaction;
import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.metadata.MetaStore;
import pl.north93.zgame.api.global.network.players.IPlayer;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;

public class TransactionImpl implements ITransaction
{
    private final ICurrency           currency;
    private final IPlayerTransaction  playerTransaction;
    private final CurrencyRankingImpl currencyRanking;
    private final MetaKey             prefix;
    private boolean isClosed;

    public TransactionImpl(final ICurrency currency, final IPlayerTransaction playerTransaction, final CurrencyRankingImpl currencyRanking)
    {
        this.currency = currency;
        this.playerTransaction = playerTransaction;
        this.currencyRanking = currencyRanking;
        this.prefix = MetaKey.get("currency:" + this.currency.getName());
    }

    @Override
    public IPlayer getAssociatedPlayer()
    {
        this.checkClosed();
        return this.playerTransaction.getPlayer();
    }

    @Override
    public boolean has(final double amount)
    {
        return this.getAmount() >= amount;
    }

    @Override
    public double add(final double amount)
    {
        final MetaStore metaStore = this.getAssociatedPlayer().getMetaStore();
        final double current = metaStore.contains(this.prefix) ? metaStore.getDouble(this.prefix) : this.currency.getStartValue();
        metaStore.setDouble(this.prefix, current + amount);
        return current;
    }

    @Override
    public double remove(final double amount)
    {
        final MetaStore metaStore = this.getAssociatedPlayer().getMetaStore();
        final double current = metaStore.contains(this.prefix) ? metaStore.getDouble(this.prefix) : this.currency.getStartValue();
        metaStore.setDouble(this.prefix, current - amount);
        return current;
    }

    @Override
    public double getAmount()
    {
        final MetaStore metaStore = this.getAssociatedPlayer().getMetaStore();
        if (metaStore.contains(this.prefix))
        {
            return metaStore.getDouble(this.prefix);
        }
        else
        {
            return this.currency.getStartValue();
        }
    }

    @Override
    public void setAmount(final double newAmount)
    {
        this.checkClosed();
        this.getAssociatedPlayer().getMetaStore().setDouble(this.prefix, newAmount);
    }

    @Override
    public void close() throws Exception
    {
        this.currencyRanking.update(this.getAssociatedPlayer().getUuid(), this.getAmount());
        this.isClosed = true;
        this.playerTransaction.close();
    }

    private void checkClosed()
    {
        if (this.isClosed)
        {
            throw new RuntimeException("Transaction is already closed");
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("currency", this.currency).append("playerTransaction", this.playerTransaction).append("isClosed", this.isClosed).toString();
    }
}
