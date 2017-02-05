package pl.north93.zgame.auth.bungee;

import java.util.logging.Logger;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;

public class PlayerJoinLeftListener implements Listener
{
    private Logger             logger;
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager    networkManager;
    @InjectComponent("NoPremiumAuth.Proxy")
    private AuthProxyComponent authProxy;

    @EventHandler
    public void onPlayerJoin(final PostLoginEvent event)
    {
        final IOnlinePlayer iOnlinePlayer = this.networkManager.getOnlinePlayer(event.getPlayer().getName()).get();
        if (iOnlinePlayer == null)
        {
            this.logger.warning("iOnlinePlayer == null in onPlayerJoin (NoPremiumAuth)");
            return;
        }
        if (iOnlinePlayer.isPremium())
        {
            this.authProxy.getAuthManager().setLoggedInStatus(iOnlinePlayer.getUuid(), true);
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerDisconnectEvent event)
    {
        this.authProxy.getAuthManager().deleteStatus(event.getPlayer().getUniqueId());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
