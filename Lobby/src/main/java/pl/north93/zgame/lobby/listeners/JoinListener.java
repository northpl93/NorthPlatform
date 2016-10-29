package pl.north93.zgame.lobby.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.API;

public class JoinListener implements Listener
{
    private final BukkitApiCore apiCore = (BukkitApiCore) API.getApiCore();

    @EventHandler
    public void onJoin(final PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(5);
        player.getInventory().clear();
        player.teleport(player.getWorld().getSpawnLocation());
    }
}
