package pl.north93.zgame.skyblock.server.management;

import static java.lang.System.currentTimeMillis;


import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Lock;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyblock.server.actions.TeleportPlayerToIsland;
import pl.north93.zgame.skyblock.server.world.Island;
import pl.north93.zgame.skyblock.shared.api.IslandData;
import pl.north93.zgame.skyblock.shared.api.player.SkyPlayer;

/**
 * Klasa zarządzająca serwerem pracującym w trybie lobby (czyli nie hostującym wysp)
 */
public class LobbyManager implements ISkyBlockServerManager
{
    @Inject
    private INetworkManager     networkManager;
    @Inject
    private IObservationManager observer;
    @Inject
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
        final Value<IOnlinePlayer> networkPlayer = this.networkManager.getPlayers().unsafe().getOnline(player.getName());

        networkPlayer.get().connectTo(this.networkManager.getServers().withUuid(islandData.getServerId()), new TeleportPlayerToIsland(islandId));
    }

    @Override
    public void tpPlayerToSpawn(final Player player)
    {
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
    }

    @Override
    public boolean canGenerateIsland(final SkyPlayer skyPlayer)
    {
        final long islandCooldown = skyPlayer.getIslandCooldown();
        return islandCooldown == 0 || (currentTimeMillis() - islandCooldown) > this.server.getSkyBlockConfig().getIslandGenerateCooldown();
    }

    @Override
    public Island getIslandAt(final Location location)
    {
        return null; // na lobby nie ma wysp więc zawsze bęzie null
    }
}
