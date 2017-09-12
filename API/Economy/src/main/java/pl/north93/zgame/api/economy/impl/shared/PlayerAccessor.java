package pl.north93.zgame.api.economy.impl.shared;

import pl.north93.zgame.api.economy.ICurrency;
import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.metadata.MetaStore;
import pl.north93.zgame.api.global.network.players.IPlayer;

class PlayerAccessor
{
    private final IPlayer   player;
    private final ICurrency currency;
    private final MetaKey   prefix;

    public PlayerAccessor(final IPlayer player, final ICurrency currency)
    {
        this.player = player;
        this.currency = currency;
        this.prefix = MetaKey.get("currency:" + currency.getName());
    }

    public void setAmount(final double newAmount)
    {
        this.player.getMetaStore().setDouble(this.prefix, newAmount);
    }

    public double getAmount()
    {
        final MetaStore metaStore = this.player.getMetaStore();
        return metaStore.contains(this.prefix) ? metaStore.getDouble(this.prefix) : this.currency.getStartValue();
    }
}
