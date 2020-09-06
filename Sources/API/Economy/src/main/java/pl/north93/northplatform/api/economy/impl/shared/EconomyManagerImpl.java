package pl.north93.northplatform.api.economy.impl.shared;


import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import com.google.common.base.Preconditions;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.economy.IAccountAccessor;
import pl.north93.northplatform.api.economy.ICurrency;
import pl.north93.northplatform.api.economy.IEconomyManager;
import pl.north93.northplatform.api.economy.ITransaction;
import pl.north93.northplatform.api.economy.cfg.CurrencyConfig;
import pl.north93.northplatform.api.economy.cfg.EconomyConfig;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.players.IPlayer;
import pl.north93.northplatform.api.global.network.players.IPlayerTransaction;
import pl.north93.northplatform.api.global.network.players.IPlayersManager;
import pl.north93.northplatform.api.global.network.players.Identity;
import pl.north93.northplatform.api.global.network.players.PlayerNotFoundException;
import pl.north93.northplatform.api.global.redis.observable.IObservationManager;
import pl.north93.northplatform.api.global.redis.observable.Value;
import pl.north93.northplatform.api.global.utils.lang.CollectionUtils;

public class EconomyManagerImpl implements IEconomyManager
{
    @Inject
    private IObservationManager observation;
    @Inject
    private IPlayersManager playersManager;
    @Inject
    private ListenerHelper listenerHelper;
    // - - -
    private final Value<EconomyConfig> config;

    @Bean
    private EconomyManagerImpl()
    {
        this.config = this.observation.get(EconomyConfig.class, "economy_config");
    }

    @Override
    public CurrencyConfig getCurrency(final String name)
    {
        Preconditions.checkNotNull(name, "Currency name can't be null");
        return CollectionUtils.findInCollection(this.config.get().getCurrencies(), CurrencyConfig::getName, name);
    }

    @Override
    public Collection<ICurrency> getCurrencies()
    {
        final EconomyConfig config = this.config.get();
        return Collections.unmodifiableCollection(config.getCurrencies());
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
        final Optional<IPlayer> optionalPlayer = this.playersManager.unsafe().get(identity);
        return optionalPlayer.map(player -> new PlayerAccessor(player, currency)).orElse(null);
    }

    @Override
    public ITransaction openTransaction(final ICurrency currency, final Identity identity) throws PlayerNotFoundException
    {
        Preconditions.checkNotNull(currency, "Currency can't be null");
        Preconditions.checkNotNull(identity, "Identity can't be null");
        final IPlayerTransaction transaction = this.playersManager.transaction(identity);
        return new TransactionImpl(currency, transaction, this.getRanking(currency), this.listenerHelper);
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
