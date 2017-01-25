package pl.north93.zgame.api.global.network.impl;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.network.players.IPlayer;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.redis.observable.Lock;

class PlayerTransactionImpl implements IPlayerTransaction
{
    private final IPlayer player;
    private final Lock    lock;
    private boolean isClosed;

    public PlayerTransactionImpl(final IPlayer player, final Lock lock)
    {
        this.player = player;
        this.lock = lock;
    }

    @Override
    public boolean isOnline()
    {
        this.checkClosed();
        return this.player != null && this.player.isOnline();
    }

    @Override
    public IPlayer getPlayer()
    {
        this.checkClosed();
        return this.player;
    }

    @Override
    public void close() throws Exception
    {
        this.checkClosed();
        this.lock.unlock();
        this.isClosed = true;
    }

    private void checkClosed()
    {
        if (this.isClosed)
        {
            throw new RuntimeException("Transaction already is closed");
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("player", this.player).append("isClosed", this.isClosed).toString();
    }
}
