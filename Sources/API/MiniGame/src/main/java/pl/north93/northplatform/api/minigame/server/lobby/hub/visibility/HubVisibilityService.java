package pl.north93.northplatform.api.minigame.server.lobby.hub.visibility;

import static java.util.Optional.ofNullable;


import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.minigame.server.MiniGameServer;
import pl.north93.northplatform.api.minigame.server.lobby.LobbyManager;
import pl.north93.northplatform.api.minigame.server.lobby.hub.HubWorld;
import pl.north93.northplatform.api.bukkit.BukkitApiCore;
import pl.north93.northplatform.api.bukkit.Main;
import pl.north93.northplatform.api.bukkit.player.IBukkitPlayers;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.global.component.annotations.bean.Aggregator;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.metadata.MetaKey;
import pl.north93.northplatform.api.global.network.players.IPlayerTransaction;

public class HubVisibilityService
{
    private static final MetaKey VISIBILITY_META = MetaKey.get("hub_visibility");
    private final Map<String, IHubVisibilityPolicy> policies = new HashMap<>();
    @Inject
    private BukkitApiCore  apiCore;
    @Inject
    private IBukkitPlayers players;
    @Inject
    private MiniGameServer server;

    @Bean
    private HubVisibilityService()
    {
    }

    @Aggregator(IHubVisibilityPolicy.class)
    private void registerPolicy(final IHubVisibilityPolicy policy)
    {
        final String name = policy.getClass().getName();
        this.policies.put(name, policy);
    }

    public void setPolicy(final INorthPlayer northPlayer, final IHubVisibilityPolicy policy)
    {
        try (final IPlayerTransaction t = northPlayer.openTransaction())
        {
            final String name = policy.getClass().getName();
            t.getPlayer().getMetaStore().set(VISIBILITY_META, name);
        }
        this.refreshVisibility(northPlayer);
    }

    public IHubVisibilityPolicy getPolicy(final INorthPlayer player)
    {
        final String policy = player.getMetaStore().get(VISIBILITY_META);
        return ofNullable(policy).map(this.policies::get).orElse(DefaultHubVisibilityPolicy.INSTANCE);
    }

    public void refreshVisibility(final INorthPlayer player)
    {
        final Main plugin = this.apiCore.getPluginMain();
        this.players.getStream().forEach(other ->
        {
            if (other.equals(player))
            {
                return;
            }

            if (this.isVisible(player, other))
            {
                player.showPlayer(plugin, other);
            }
            else
            {
                player.hidePlayer(plugin, other);
            }

            if (this.isVisible(other, player))
            {
                other.showPlayer(plugin, player);
            }
            else
            {
                other.hidePlayer(plugin, player);
            }
        });
    }

    private boolean isVisible(final INorthPlayer observer, final INorthPlayer target)
    {
        final HubWorld hubOfObserver = this.getHubOf(observer);
        final HubWorld hubOfTarget = this.getHubOf(target);

        if (! hubOfObserver.equals(hubOfTarget))
        {
            return false;
        }

        final IHubVisibilityPolicy policy = this.getPolicy(observer);
        return policy.visible(observer, target);
    }

    private HubWorld getHubOf(final Player player)
    {
        final LobbyManager lobbyManager = this.server.getServerManager();
        return lobbyManager.getLocalHub().getHubWorld(player);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}