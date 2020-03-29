package pl.north93.northplatform.api.bungee.proxy.impl.listener;

import static org.diorite.commons.arrays.DioriteArrayUtils.EMPTY_OBJECT;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import pl.north93.northplatform.api.bungee.proxy.event.HandlePlayerProxyJoinEvent;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.network.INetworkManager;
import pl.north93.northplatform.api.global.network.JoiningPolicy;
import pl.north93.northplatform.api.global.network.NetworkMeta;
import pl.north93.northplatform.api.global.network.players.IOnlinePlayer;
import pl.north93.northplatform.api.global.redis.observable.Value;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.permissions.Group;

public class JoinPermissionsChecker implements Listener
{
    @Inject @Messages("Messages")
    private MessagesBox     messages;
    @Inject
    private INetworkManager networkManager;

    @EventHandler
    public void checkJoinConditions(final HandlePlayerProxyJoinEvent event)
    {
        final Value<IOnlinePlayer> player = event.getValue();
        final IOnlinePlayer cache = player.get();

        final NetworkMeta networkMeta = this.networkManager.getNetworkConfig().get();
        if (networkMeta == null)
        {
            event.setCancelled(this.messages.getComponent("pl-PL", "kick.generic_error", "networkMeta==null"));
            return;
        }

        final JoiningPolicy joiningPolicy = networkMeta.joiningPolicy;
        final Group group = cache.getGroup();

        if (joiningPolicy == JoiningPolicy.NOBODY)
        {
            event.setCancelled(this.messages.getComponent("pl-PL", "join.access_locked", EMPTY_OBJECT));
        }
        else if (joiningPolicy == JoiningPolicy.ONLY_ADMIN && ! group.hasPermission("join.admin")) // wpuszczanie tylko adminów
        {
            event.setCancelled(this.messages.getComponent("pl-PL", "join.access_locked", EMPTY_OBJECT));
        }
        else if (joiningPolicy == JoiningPolicy.ONLY_VIP && ! group.hasPermission("join.vip"))
        {
            event.setCancelled(this.messages.getComponent("pl-PL", "join.access_locked", EMPTY_OBJECT));
        }
        else
        {
            final int onlinePlayersCount = this.networkManager.getProxies().onlinePlayersCount();
            if (onlinePlayersCount > networkMeta.displayMaxPlayers && ! group.hasPermission("join.bypass"))
            {
                event.setCancelled(this.messages.getComponent("pl-PL", "join.server_full", EMPTY_OBJECT));
            }
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
