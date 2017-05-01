package pl.north93.zgame.api.bungee.connection;

import static java.util.ResourceBundle.getBundle;

import static net.md_5.bungee.api.ChatColor.translateAlternateColorCodes;
import static net.md_5.bungee.api.chat.TextComponent.fromLegacyText;


import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.north93.zgame.api.bungee.BungeeApiCore;
import pl.north93.zgame.api.global.network.NetworkMeta;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.messages.UTF8Control;

public class NorthReconnectHandler implements ReconnectHandler
{
    private final BungeeApiCore   apiCore;
    private final INetworkManager networkManager;
    private final ResourceBundle  messages;

    public NorthReconnectHandler(final BungeeApiCore apiCore)
    {
        this.apiCore = apiCore;
        this.networkManager = apiCore.getNetworkManager();
        this.messages = getBundle("Messages", Locale.getDefault(), this.getClass().getClassLoader(), new UTF8Control());
    }

    @Override
    public ServerInfo getServer(final ProxiedPlayer proxiedPlayer)
    {
        final NetworkMeta meta = this.networkManager.getNetworkMeta().get();
        final Server server = this.apiCore.getConnectionManager().getBestServerFromServersGroup(meta.defaultServersGroup);
        if (server == null)
        {
            final String message = this.messages.getString("join.no_servers");
            proxiedPlayer.disconnect(fromLegacyText(translateAlternateColorCodes('&', message)));
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
