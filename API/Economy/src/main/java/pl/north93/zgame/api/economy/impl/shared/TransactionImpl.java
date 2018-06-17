package pl.north93.zgame.api.economy.impl.shared;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.economy.ICurrency;
import pl.north93.zgame.api.economy.ITransaction;
import pl.north93.zgame.api.global.network.players.IPlayer;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;

class TransactionImpl implements ITransaction
{
    private final IPlayerTransaction  playerTransaction;
    private final PlayerAccessor      playerAccessor;
    private final CurrencyRankingImpl currencyRanking;
    private final ListenerHelper      listener;
    private boolean isClosed;

    public TransactionImpl(final ICurrency currency, final IPlayerTransaction playerTransaction, final CurrencyRankingImpl currencyRanking, final ListenerHelper listener)
    {
        this.playerTransaction = playerTransaction;
        this.playerAccessor = new PlayerAccessor(playerTransaction.getPlayer(), currency);
        this.currencyRanking = currencyRanking;
        this.listener = listener;
    }

    @Override
    public IPlayer getAssociatedPlayer()
    {
        this.checkClosed();
        return this.playerTransaction.getPlayer();
    }

    @Override
    public ICurrency getCurrency()
    {
        this.checkClosed();
        return this.playerAccessor.getCurrency();
    }

    @Override
    public boolean has(final double amount)
    {
        return this.getAmount() >= amount;
    }

    @Override
    public double add(final double amount)
    {
        this.checkClosed();
        final double current = this.playerAccessor.getAmount();
        this.playerAccessor.setAmount(current + amount);
        return current;
    }

    @Override
    public double remove(final double amount)
    {
        this.checkClosed();
        final double current = this.playerAccessor.getAmount();
        this.playerAccessor.setAmount(current - amount);
        return current;
    }

    @Override
    public double getAmount()
    {
        this.checkClosed();
        return this.playerAccessor.getAmount();
    }

    @Override
    public void setAmount(final double newAmount)
    {
        this.checkClosed();
        this.playerAccessor.setAmount(newAmount);
    }

    @Override
    public boolean isTransactionOpen()
    {
        return ! this.isClosed;
    }

    @Override
    public void close() throws Exception
    {
        this.checkClosed();

        // wywolujemy listenery
        final IPlayer player = this.getAssociatedPlayer();
        this.listener.amountUpdated(player, this.getCurrency(), this.getAmount());

        // aktualizujemy dane w rankingu
        this.currencyRanking.update(player.getUuid(), this.getAmount());

        // zamykamy transakcje
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("playerTransaction", this.playerTransaction).append("isClosed", this.isClosed).toString();
    }
}
