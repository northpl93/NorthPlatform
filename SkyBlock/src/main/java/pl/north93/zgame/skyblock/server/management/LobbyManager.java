package pl.north93.zgame.skyblock.server.management;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Lock;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.skyblock.api.IslandData;
import pl.north93.zgame.skyblock.server.actions.TeleportPlayerToIsland;
import pl.north93.zgame.skyblock.server.SkyBlockServer;

/**
 * Klasa zarządzająca serwerem pracującym w trybie lobby (czyli nie hostującym wysp)
 */
public class LobbyManager implements ISkyBlockServerManager
{
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager     networkManager;
    @InjectComponent("API.Database.Redis.Observer")
    private IObservationManager observer;
    @InjectComponent("SkyBlock.Server")
    private SkyBlockServer      server;

    @Override
    public void start()
    {

    }

    @Override
    public void stop()
    {

    }

    @Override
    public Lock getIslandDataLock(final UUID islandId)
    {
        return this.observer.getLock("lock:isldata:" + islandId);
    }

    @Override
    public void tpPlayerToIsland(final Player player, final UUID islandId)
    {
        final IslandData islandData = this.server.getIslandDao().getIsland(islandId);
        final Value<IOnlinePlayer> networkPlayer = this.networkManager.getOnlinePlayer(player.getName());

        networkPlayer.get().connectTo(this.networkManager.getServer(islandData.getServerId()).get(), new TeleportPlayerToIsland(islandId));
    }

    @Override
    public void tpPlayerToSpawn(final Player player)
    {
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
    }
}
