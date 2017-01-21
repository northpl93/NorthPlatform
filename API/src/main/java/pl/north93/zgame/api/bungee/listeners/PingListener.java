package pl.north93.zgame.api.bungee.listeners;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.messages.NetworkMeta;

public class PingListener implements Listener
{
    private final ApiCore core = API.getApiCore();

    @EventHandler
    public void onPing(final ProxyPingEvent event)
    {
        final NetworkMeta networkMeta = this.core.getNetworkManager().getNetworkMeta().get();
        final ServerPing response = event.getResponse();
        if (networkMeta == null)
        {
            response.setDescriptionComponent(new TextComponent(TextComponent.fromLegacyText("&cProblemy techniczne. (networkMeta==null in onPing)")));
            this.core.getLogger().severe("networkMeta is null in onPing");
            return;
        }

        response.setDescriptionComponent(new TextComponent(TextComponent.fromLegacyText(networkMeta.serverListMotd)));

        final ServerPing.Players players = response.getPlayers();
        players.setSample(null);
        players.setMax(networkMeta.displayMaxPlayers);
        players.setOnline(this.core.getNetworkManager().onlinePlayersCount());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("core", this.core).toString();
    }
}
