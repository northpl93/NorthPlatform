package pl.north93.zgame.api.economy.impl.client;

import static pl.north93.zgame.api.global.utils.CollectionUtils.findInCollection;


import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.economy.ICurrency;
import pl.north93.zgame.api.economy.IEconomyManager;
import pl.north93.zgame.api.economy.ITransaction;
import pl.north93.zgame.api.economy.cfg.CurrencyConfig;
import pl.north93.zgame.api.economy.cfg.EconomyConfig;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.PostInject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Value;

public class EconomyManagerImpl implements IEconomyManager
{
    @InjectComponent("API.Database.Redis.Observer")
    private IObservationManager  observation;
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager      networkManager;
    // - - -
    private Value<EconomyConfig> config;

    @PostInject
    private void init()
    {
        this.config = this.observation.get(EconomyConfig.class, "economy_config");
    }

    @Override
    public CurrencyConfig getCurrency(final String name)
    {
        return findInCollection(this.config.get().getCurrencies(), CurrencyConfig::getName, name);
    }

    @Override
    public ITransaction openTransaction(final ICurrency currency, final UUID playerId)
    {
        final IPlayerTransaction transaction = this.networkManager.getPlayers().transaction(playerId);
        return new TransactionImpl(currency, transaction);
    }

    @Override
    public ITransaction openTransaction(final ICurrency currency, final String playerName)
    {
        final IPlayerTransaction transaction = this.networkManager.getPlayers().transaction(playerName);
        return new TransactionImpl(currency, transaction);
    }

    public void setConfig(final EconomyConfig economyConfig)
    {
        this.config.set(economyConfig);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
