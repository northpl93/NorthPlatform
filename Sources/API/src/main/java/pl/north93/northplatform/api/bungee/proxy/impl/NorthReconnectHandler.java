package pl.north93.northplatform.api.bungee.proxy.impl;

import static java.util.ResourceBundle.getBundle;


import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.north93.northplatform.api.bukkit.utils.chat.ChatUtils;
import pl.north93.northplatform.api.global.messages.UTF8Control;
import pl.north93.northplatform.api.global.network.INetworkManager;
import pl.north93.northplatform.api.global.network.NetworkMeta;
import pl.north93.northplatform.api.global.network.server.IServersManager;
import pl.north93.northplatform.api.global.network.server.Server;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;

@Slf4j
/*default*/ class NorthReconnectHandler implements ReconnectHandler
{
    private final INetworkManager networkManager;
    private final IServersManager serversManager;
    private final ResourceBundle  messages;

    @Bean
    private NorthReconnectHandler(final INetworkManager networkManager, final IServersManager serversManager)
    {
        ProxyServer.getInstance().setReconnectHandler(this);
        this.networkManager = networkManager;
        this.serversManager = serversManager;
        this.messages = getBundle("Messages", Locale.getDefault(), this.getClass().getClassLoader(), new UTF8Control());
    }

    @Override
    public ServerInfo getServer(final ProxiedPlayer proxiedPlayer)
    {
        final NetworkMeta meta = this.networkManager.getNetworkConfig().get();
        if (meta == null)
        {
            log.error("Can't find best server for player {} because network meta is null", proxiedPlayer.getName());
            return null;
        }

        final Server server = this.serversManager.getLeastLoadedServerInGroup(meta.defaultServersGroup);
        if (server == null)
        {
            final String message = this.messages.getString("join.no_servers");
            proxiedPlayer.disconnect(ChatUtils.fromLegacyText(message));

            log.warn("Player {} disconnected because there is no good server to connect in group {}", proxiedPlayer.getName(), meta.defaultServersGroup);
            return null;
        }

        return ProxyServer.getInstance().getServerInfo(server.getProxyName());
    }

    @Override
    public void setServer(final ProxiedPlayer proxiedPlayer)
    {
    }

    @Override
    public void save()
    {
    }

    @Override
    public void close()
    {
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
