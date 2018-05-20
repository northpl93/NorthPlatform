package pl.north93.zgame.api.bungee.proxy.impl.listener;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import pl.north93.zgame.api.bungee.proxy.event.HandlePlayerProxyJoinEvent;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.JoiningPolicy;
import pl.north93.zgame.api.global.network.NetworkMeta;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.Value;

public class JoinPermissionsChecker implements Listener
{
    private static final MetaKey BAN_EXPIRE = MetaKey.get("banExpireAt");
    @Inject @Messages("Messages")
    private MessagesBox     apiMessages;
    @Inject
    private INetworkManager networkManager;

    @EventHandler
    public void checkJoinConditions(final HandlePlayerProxyJoinEvent event)
    {
        final Value<IOnlinePlayer> player = event.getValue();
        final IOnlinePlayer cache = player.get();

        final NetworkMeta networkMeta = this.networkManager.getNetworkConfig().get();
        final JoiningPolicy joiningPolicy = networkMeta.joiningPolicy;
        if (joiningPolicy == JoiningPolicy.NOBODY)
        {
            event.setCancelled(this.apiMessages.getMessage("pl-PL", "join.access_locked", (Object[]) null));
        }
        else if (joiningPolicy == JoiningPolicy.ONLY_ADMIN && ! cache.getGroup().hasPermission("join.admin")) // wpuszczanie tylko adminów
        {
            event.setCancelled(this.apiMessages.getMessage("pl-PL", "join.access_locked", (Object[]) null));
        }
        else if (joiningPolicy == JoiningPolicy.ONLY_VIP && ! cache.getGroup().hasPermission("join.vip"))
        {
            event.setCancelled(this.apiMessages.getMessage("pl-PL", "join.access_locked", (Object[]) null));
        }
        else if (this.networkManager.getProxies().onlinePlayersCount() > networkMeta.displayMaxPlayers && ! cache.getGroup().hasPermission("join.bypass"))
        {
            event.setCancelled(this.apiMessages.getMessage("pl-PL", "join.server_full", (Object[]) null));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void logPlayerJoin(final HandlePlayerProxyJoinEvent event)
    {
        final IOnlinePlayer cache = event.getValue().get();

        final String hostAddress = event.getConnection().getAddress().getHostString();
        this.networkManager.getPlayers().getInternalData().logPlayerJoin(cache.getUuid(), cache.getNick(), cache.isPremium(), hostAddress, cache.getProxyId());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
