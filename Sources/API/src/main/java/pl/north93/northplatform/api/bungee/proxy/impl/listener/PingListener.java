package pl.north93.northplatform.api.bungee.proxy.impl.listener;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.north93.northplatform.api.bukkit.utils.chat.ChatUtils;
import pl.north93.northplatform.api.global.network.INetworkManager;
import pl.north93.northplatform.api.global.network.NetworkMeta;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.proxy.IProxiesManager;

@Slf4j
public class PingListener implements Listener
{
    @Inject
    private INetworkManager networkManager;
    @Inject
    private IProxiesManager proxiesManager;

    @EventHandler
    public void onPing(final ProxyPingEvent event)
    {
        final NetworkMeta networkMeta = this.networkManager.getNetworkConfig().get();
        final ServerPing response = event.getResponse();
        if (networkMeta == null)
        {
            response.setDescriptionComponent(ChatUtils.fromLegacyText(ChatColor.RED + "Problemy techniczne. (networkMeta==null in onPing)"));
            log.error("networkMeta is null in onPing");
            return;
        }

        response.setDescriptionComponent(ChatUtils.fromLegacyText(networkMeta.serverListMotd));

        final ServerPing.Players players = response.getPlayers();
        players.setSample(null);
        players.setMax(networkMeta.displayMaxPlayers);
        players.setOnline(this.proxiesManager.onlinePlayersCount());

        final ServerPing.Protocol version = response.getVersion();
        version.setName(ChatUtils.translateAlternateColorCodes(networkMeta.serverListVersion));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
