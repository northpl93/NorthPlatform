package pl.north93.zgame.api.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import pl.north93.zgame.api.global.network.ProxyRpc;

public class ProxyRpcImpl implements ProxyRpc
{
    @Override
    public void sendMessage(final String nick, final String message)
    {
        ProxyServer.getInstance().getPlayer(nick).sendMessage(TextComponent.fromLegacyText(message));
    }

    @Override
    public void kick(final String nick, final String kickMessage)
    {
        ProxyServer.getInstance().getPlayer(nick).disconnect(TextComponent.fromLegacyText(kickMessage));
    }
}
