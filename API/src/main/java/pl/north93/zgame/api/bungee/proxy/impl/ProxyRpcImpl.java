package pl.north93.zgame.api.bungee.proxy.impl;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.north93.zgame.api.bukkit.utils.ChatUtils;
import pl.north93.zgame.api.bungee.BungeeApiCore;
import pl.north93.zgame.api.bungee.proxy.IProxyServerManager;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.proxy.IProxyRpc;
import pl.north93.zgame.api.global.network.server.ServerProxyData;
import pl.north93.zgame.api.global.network.server.joinaction.JoinActionsContainer;

class ProxyRpcImpl implements IProxyRpc
{
    @Inject
    private BungeeApiCore apiCore;
    @Inject
    private IProxyServerManager proxyServerManager;
    private final ProxyServer proxy = ProxyServer.getInstance();

    @Override
    public Boolean isOnline(final String nick)
    {
        return this.proxy.getPlayer(nick) != null;
    }

    @Override
    public void sendMessage(final String nick, final String message, final Boolean colorText)
    {
        final ProxiedPlayer player = this.proxy.getPlayer(nick);
        if (colorText)
        {
            player.sendMessage(TextComponent.fromLegacyText(ChatUtils.translateAlternateColorCodes(message)));
            return;
        }

        player.sendMessage(TextComponent.fromLegacyText(message));
    }

    @Override
    public void kick(final String nick, final String kickMessage)
    {
        this.proxy.getPlayer(nick).disconnect(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', kickMessage)));
    }

    @Override
    public void connectPlayer(final String nick, final String serverName, final JoinActionsContainer actions)
    {   
        this.apiCore.getConnectionManager().connectPlayerToServer(this.proxy.getPlayer(nick), serverName, actions);
    }

    @Override
    public void connectPlayerToServersGroup(final String nick, final String serversGroup, final JoinActionsContainer actions)
    {
        this.apiCore.getConnectionManager().connectPlayerToServersGroup(this.proxy.getPlayer(nick), serversGroup, actions);
    }

    @Override
    public void addServer(final ServerProxyData proxyData)
    {
        this.proxyServerManager.getServerList().addServer(proxyData);
    }

    @Override
    public void removeServer(final ServerProxyData proxyData)
    {
        this.proxyServerManager.getServerList().removeServer(proxyData);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
