package pl.north93.zgame.skyblock.server.management;

import java.util.UUID;

import org.bukkit.entity.Player;

public interface ISkyBlockServerManager
{
    void start();

    void stop();

    void tpPlayerToIsland(Player player, UUID islandId);
}
