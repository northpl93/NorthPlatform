package pl.north93.zgame.api.bungee.proxy.impl;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.server.joinaction.JoinActionsContainer;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Value;

/**
 * Klasa zarządzająca łączeniem graczy z konkretnymi serwerami.
 */
/*default*/ class ConnectionManagerImpl implements pl.north93.zgame.api.bungee.proxy.IConnectionManager
{
    @Inject
    private IObservationManager observationManager;

    @Bean
    private ConnectionManagerImpl()
    {
    }

    @Override
    public void connectPlayerToServer(final ProxiedPlayer player, final String serverName, final JoinActionsContainer actions)
    {
        if (! actions.isEmpty())
        {
            final Value<JoinActionsContainer> value = this.observationManager.get(JoinActionsContainer.class, "serveractions:" + player.getName());
            value.setExpire(actions, 10, TimeUnit.SECONDS);
        }
        player.connect(ProxyServer.getInstance().getServerInfo(serverName));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
