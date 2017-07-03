package pl.arieals.minigame.bedwars.listener;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;

public class InvisibleListener implements Listener
{
    @EventHandler
    public void onInteract(final PlayerInteractEvent event)
    {
        final Player player = event.getPlayer();
        final BedWarsPlayer playerData = getPlayerData(player, BedWarsPlayer.class);

        if (playerData == null || ! playerData.isAlive())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(final EntityDamageByEntityEvent event)
    {
        if (! (event.getDamager() instanceof Player))
        {
            return;
        }

        final Player damager = (Player) event.getDamager();

        final BedWarsPlayer playerData = getPlayerData(damager, BedWarsPlayer.class);
        if (playerData == null || ! playerData.isAlive())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemPickup(final PlayerPickupItemEvent event)
    {
        final BedWarsPlayer playerData = getPlayerData(event.getPlayer(), BedWarsPlayer.class);
        if (playerData == null || ! playerData.isAlive())
        {
            event.setCancelled(true);
        }
    }
}
