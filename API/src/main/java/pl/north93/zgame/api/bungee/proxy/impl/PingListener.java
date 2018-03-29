package pl.north93.zgame.api.bungee.proxy.impl;

import static pl.north93.zgame.api.bukkit.utils.chat.ChatUtils.fromLegacyText;
import static pl.north93.zgame.api.bukkit.utils.chat.ChatUtils.translateAlternateColorCodes;


import java.util.logging.Logger;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.NetworkMeta;

public class PingListener implements Listener
{
    @Inject
    private Logger logger;
    @Inject
    private INetworkManager networkManager;

    @EventHandler
    public void onPing(final ProxyPingEvent event)
    {
        final NetworkMeta networkMeta = this.networkManager.getNetworkConfig().get();
        final ServerPing response = event.getResponse();
        if (networkMeta == null)
        {
            response.setDescriptionComponent(fromLegacyText(ChatColor.RED + "Problemy techniczne. (networkMeta==null in onPing)"));
            this.logger.severe("networkMeta is null in onPing");
            return;
        }

        response.setDescriptionComponent(fromLegacyText(networkMeta.serverListMotd));

        final ServerPing.Players players = response.getPlayers();
        players.setSample(null);
        players.setMax(networkMeta.displayMaxPlayers);
        players.setOnline(this.networkManager.getProxies().onlinePlayersCount());

        final ServerPing.Protocol version = response.getVersion();
        version.setName(translateAlternateColorCodes(networkMeta.serverListVersion));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
