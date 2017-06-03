package pl.north93.zgame.skyplayerexp.server.compass;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.metadata.MetaStore;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.skyblock.shared.api.ServerMode;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyplayerexp.server.ExperienceServer;

public class CompassConnector implements Listener
{
    private static final MetaKey COMPASS_ENABLED = MetaKey.get("sky:lobbyCompassEnabled");
    @Inject
    private INetworkManager  networkManager;
    @Inject
    private SkyBlockServer   server;
    @Inject
    private ExperienceServer experience;

    @EventHandler
    public void enableCompassOnJoin(final PlayerJoinEvent event)
    {
        if (! this.isLobby())
        {
            return; // do not enable in island host
        }

        final Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.SURVIVAL)
        {
            return; // do not enable compass in other modes than survival
        }

        if (this.compassEnabledInConfig(player.getName()))
        {
            this.experience.getCompassManager().switchCompassState(player, true);
        }
    }

    public boolean isLobby()
    {
        return this.server.getServerMode() == ServerMode.LOBBY;
        //return true; // useful for debug
    }

    public boolean compassEnabledInConfig(final String player)
    {
        final MetaStore metaStore = this.networkManager.getOnlinePlayer(player).get().getMetaStore();
        if (metaStore.contains(COMPASS_ENABLED))
        {
            return metaStore.getBoolean(COMPASS_ENABLED);
        }
        return true;
    }

    public void switchCompassStateInConfig(final String player, final boolean newState)
    {
        this.networkManager.getPlayers().access(player, iplayer ->
        {
            final MetaStore metaStore = iplayer.getMetaStore();
            metaStore.setBoolean(COMPASS_ENABLED, newState);
        });
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
