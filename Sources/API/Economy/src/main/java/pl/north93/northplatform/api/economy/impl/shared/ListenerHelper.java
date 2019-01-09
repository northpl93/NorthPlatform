package pl.north93.northplatform.api.economy.impl.shared;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.economy.ICurrency;
import pl.north93.northplatform.api.economy.ITransactionListener;
import pl.north93.northplatform.api.global.component.annotations.bean.Aggregator;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.network.players.IPlayer;

/*default*/ class ListenerHelper
{
    private final List<ITransactionListener> listeners = new ArrayList<>();

    @Bean
    private ListenerHelper()
    {
    }

    @Aggregator(ITransactionListener.class)
    public void registerListener(final ITransactionListener listener)
    {
        this.listeners.add(listener);
    }

    public void amountUpdated(final IPlayer player, final ICurrency currency, final double newAmount)
    {
        this.listeners.forEach(listener -> listener.amountUpdated(player, currency, newAmount));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("listeners", this.listeners).toString();
    }
}
