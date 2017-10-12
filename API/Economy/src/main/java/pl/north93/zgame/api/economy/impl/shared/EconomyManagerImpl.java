package pl.north93.zgame.api.economy.impl.shared;

import static pl.north93.zgame.api.global.utils.lang.CollectionUtils.findInCollection;


import com.google.common.base.Preconditions;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.economy.IAccountAccessor;
import pl.north93.zgame.api.economy.ICurrency;
import pl.north93.zgame.api.economy.IEconomyManager;
import pl.north93.zgame.api.economy.ITransaction;
import pl.north93.zgame.api.economy.cfg.CurrencyConfig;
import pl.north93.zgame.api.economy.cfg.EconomyConfig;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.exceptions.PlayerNotFoundException;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IPlayer;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Value;

public class EconomyManagerImpl implements IEconomyManager
{
    @Inject
    private IObservationManager  observation;
    @Inject
    private INetworkManager      networkManager;
    // - - -
    private Value<EconomyConfig> config;

    @Bean
    private EconomyManagerImpl()
    {
        this.config = this.observation.get(EconomyConfig.class, "economy_config");
    }

    @Override
    public CurrencyConfig getCurrency(final String name)
    {
        Preconditions.checkNotNull(name, "Currency name can't be null");
        return findInCollection(this.config.get().getCurrencies(), CurrencyConfig::getName, name);
    }

    @Override
    public CurrencyRankingImpl getRanking(final ICurrency currency)
    {
        Preconditions.checkNotNull(currency, "Currency can't be null");
        return new CurrencyRankingImpl(currency.getName());
    }

    @Override
    public IAccountAccessor getUnsafeAccessor(final ICurrency currency, final Identity identity)
    {
        final IPlayer player = this.networkManager.getPlayers().unsafe().get(identity);
        return new PlayerAccessor(player, currency);
    }

    @Override
    public ITransaction openTransaction(final ICurrency currency, final Identity identity) throws PlayerNotFoundException
    {
        Preconditions.checkNotNull(currency, "Currency can't be null");
        Preconditions.checkNotNull(identity, "Identity can't be null");
        final IPlayerTransaction transaction = this.networkManager.getPlayers().transaction(identity);
        return new TransactionImpl(currency, transaction, this.getRanking(currency));
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
