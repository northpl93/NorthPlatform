package pl.north93.zgame.api.global.network.impl.players;

import java.util.function.Consumer;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.network.players.IPlayer;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.redis.observable.Lock;
import pl.north93.zgame.api.global.redis.observable.Value;

class PlayerTransactionImpl implements IPlayerTransaction
{
    private final IPlayer           player;
    private final Lock              lock;
    private final Consumer<IPlayer> callback;
    private       boolean           isClosed;

    public PlayerTransactionImpl(final Value<? extends IPlayer> playerValue, final Lock lock, final Consumer<IPlayer> callback)
    {
        //noinspection unchecked
        this.player = playerValue.get();
        this.lock = lock;
        this.callback = callback;
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
    public void close()
    {
        this.checkClosed();
        try
        {
            this.isClosed = true;
            this.callback.accept(this.player);
        }
        finally
        {
            // jesli zostanie rzucony wyjątek w callbacku to i tak powinniśmy zwolnić
            // locka, aby zapobiec dead-lockom.
            this.lock.unlock();
        }
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
