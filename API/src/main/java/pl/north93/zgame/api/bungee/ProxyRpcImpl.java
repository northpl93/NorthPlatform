package pl.north93.zgame.api.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import pl.north93.zgame.api.global.network.ProxyRpc;

public class ProxyRpcImpl implements ProxyRpc
{
    private final ProxyServer proxy = ProxyServer.getInstance();

    @Override
    public void sendMessage(final String nick, final String message)
    {
        this.proxy.getPlayer(nick).sendMessage(TextComponent.fromLegacyText(message));
    }

    @Override
    public void kick(final String nick, final String kickMessage)
    {
        this.proxy.getPlayer(nick).disconnect(TextComponent.fromLegacyText(kickMessage));
    }

    @Override
    public void connectPlayer(final String nick, final String serverName)
    {
        this.proxy.getPlayer(nick).connect(this.proxy.getServerInfo(serverName));
    }
}
