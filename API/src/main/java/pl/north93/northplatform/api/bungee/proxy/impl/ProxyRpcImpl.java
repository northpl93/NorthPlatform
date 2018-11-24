package pl.north93.northplatform.api.bungee.proxy.impl;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.packet.Chat;
import net.md_5.bungee.protocol.packet.Kick;
import pl.north93.northplatform.api.global.network.proxy.IProxyRpc;
import pl.north93.northplatform.api.global.network.server.Server;
import pl.north93.northplatform.api.global.network.server.joinaction.JoinActionsContainer;
import pl.north93.northplatform.api.bungee.BungeeApiCore;
import pl.north93.northplatform.api.bungee.proxy.IProxyServerManager;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

class ProxyRpcImpl implements IProxyRpc
{
    private static final ProxyServer                                                  PROXY = ProxyServer.getInstance();
    @Inject
    private              BungeeApiCore                                                apiCore;
    @Inject
    private              IProxyServerManager                                          proxyServerManager;
    @Inject
    private              pl.north93.northplatform.api.bungee.proxy.IConnectionManager IConnectionManager;

    @Override
    public Boolean isOnline(final String nick)
    {
        return PROXY.getPlayer(nick) != null;
    }

    @Override
    public void sendJsonMessage(final String nick, final String json)
    {
        final ProxiedPlayer player = PROXY.getPlayer(nick);
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
        final ProxiedPlayer player = PROXY.getPlayer(nick);
        if (player == null)
        {
            // gracz mogl juz sie rozlaczyc
            return;
        }

        player.unsafe().sendPacket(new Kick(json));
        //PROXY.getPlayer(nick).disconnect(ChatUtils.fromLegacyText(kickMessage));
    }

    @Override
    public void connectPlayer(final String nick, final String serverName, final JoinActionsContainer actions)
    {
        final ProxiedPlayer player = PROXY.getPlayer(nick);
        if (player == null)
        {
            // gracz mogl juz sie rozlaczyc
            return;
        }

        this.IConnectionManager.connectPlayerToServer(player, serverName, actions);
    }

    @Override
    public void addServer(final Server server)
    {
        this.proxyServerManager.getServerList().addServer(server);
    }

    @Override
    public void removeServer(final Server server)
    {
        this.proxyServerManager.getServerList().removeServer(server);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
