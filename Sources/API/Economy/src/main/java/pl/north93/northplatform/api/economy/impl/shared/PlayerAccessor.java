package pl.north93.northplatform.api.economy.impl.shared;

import lombok.ToString;
import pl.north93.northplatform.api.economy.IAccountAccessor;
import pl.north93.northplatform.api.economy.ICurrency;
import pl.north93.northplatform.api.global.metadata.MetaKey;
import pl.north93.northplatform.api.global.metadata.MetaStore;
import pl.north93.northplatform.api.global.network.players.IPlayer;

@ToString(of = {"player", "currency"})
class PlayerAccessor implements IAccountAccessor
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
        this.player.getMetaStore().set(this.prefix, newAmount);
    }

    @Override
    public IPlayer getAssociatedPlayer()
    {
        return this.player;
    }

    @Override
    public ICurrency getCurrency()
    {
        return this.currency;
    }

    @Override
    public double getAmount()
    {
        final MetaStore metaStore = this.player.getMetaStore();
        return metaStore.contains(this.prefix) ? metaStore.get(this.prefix) : this.currency.getStartValue();
    }

    @Override
    public boolean has(final double amount)
    {
        return this.getAmount() >= amount;
    }
}
