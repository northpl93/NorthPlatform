package pl.north93.zgame.skyblock.server.listeners.islandhost;

import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.skyblock.api.IslandData;
import pl.north93.zgame.skyblock.api.player.SkyPlayer;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyblock.server.management.IslandHostManager;
import pl.north93.zgame.skyblock.server.world.Island;

public class ServerJoinListener implements Listener
{
    private Logger          logger;
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager networkManager;
    @InjectComponent("SkyBlock.Server")
    private SkyBlockServer  server;

    @EventHandler
    public void onJoin(final PlayerJoinEvent event)
    {
        final Value<IOnlinePlayer> networkPlayer = this.networkManager.getOnlinePlayer(event.getPlayer().getName());
        final SkyPlayer skyPlayer = SkyPlayer.get(networkPlayer);

        final UUID islandId = skyPlayer.getIslandTpTo();
        if (islandId == null)
        {
            return;
        }

        skyPlayer.setIslandToTp(null);

        final IslandData islandData = this.server.getIslandDao().getIsland(islandId);
        if (islandData == null)
        {
            this.logger.severe("[SkyBlock] islandData is null in onJoin");
            return;
        }

        final IslandHostManager islandHostManager = this.server.getServerManager();
        final Island island = islandHostManager.getWorldManager(islandData.getIslandType()).getIslands().getById(islandId);
        if (island == null)
        {
            this.logger.severe("[SkyBlock] island is null in onJoin");
            return;
        }

        event.getPlayer().teleport(island.getHomeLocation().add(0.5, 0.5, 0.5));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
