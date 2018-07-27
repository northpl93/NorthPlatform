package pl.north93.zgame.api.bungee.proxy.impl;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.packet.Chat;
import net.md_5.bungee.protocol.packet.Kick;
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
    public void sendJsonMessage(final String nick, final String json)
    {
        final ProxiedPlayer player = this.proxy.getPlayer(nick);
        if (player == null)
        {
            // gracz mogl sie juz rozlaczyc zanim otrzymal ta wiadomosc
            return;
        }

        player.unsafe().sendPacket(new Chat(json));
    }

    @Override
    public void kick(final String nick, final String json)
    {
        final ProxiedPlayer player = this.proxy.getPlayer(nick);
        if (player == null)
        {
            // gracz mogl juz sie rozlaczyc
            return;
        }

        player.unsafe().sendPacket(new Kick(json));
        //this.proxy.getPlayer(nick).disconnect(ChatUtils.fromLegacyText(kickMessage));
    }

    @Override
    public void connectPlayer(final String nick, final String serverName, final JoinActionsContainer actions)
    {
        final ProxiedPlayer player = this.proxy.getPlayer(nick);
        if (player == null)
        {
            // gracz mogl juz sie rozlaczyc
            return;
        }

        this.apiCore.getConnectionManager().connectPlayerToServer(player, serverName, actions);
    }

    @Override
    public void connectPlayerToServersGroup(final String nick, final String serversGroup, final JoinActionsContainer actions)
    {
        final ProxiedPlayer player = this.proxy.getPlayer(nick);
        if (player == null)
        {
            // gracz mogl juz sie rozlaczyc
            return;
        }

        this.apiCore.getConnectionManager().connectPlayerToServersGroup(player, serversGroup, actions);
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
