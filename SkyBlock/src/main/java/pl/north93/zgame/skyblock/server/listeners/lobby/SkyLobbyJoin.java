package pl.north93.zgame.skyblock.server.listeners.lobby;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class SkyLobbyJoin implements Listener
{
    @EventHandler
    public void onSkyLobbyJoin(final PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();
        final World world = Bukkit.getWorlds().get(0);
        player.teleport(world.getSpawnLocation());
    }
}
