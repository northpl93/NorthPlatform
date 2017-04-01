package pl.north93.zgame.skyblock.server.management;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import pl.north93.zgame.api.global.redis.observable.Lock;
import pl.north93.zgame.skyblock.shared.api.player.SkyPlayer;
import pl.north93.zgame.skyblock.server.world.Island;

public interface ISkyBlockServerManager
{
    void start();

    void stop();

    Lock getIslandDataLock(UUID islandId);

    void tpPlayerToIsland(Player player, UUID islandId);

    void tpPlayerToSpawn(Player player);

    boolean canGenerateIsland(SkyPlayer skyPlayer);

    Island getIslandAt(Location location);
}
