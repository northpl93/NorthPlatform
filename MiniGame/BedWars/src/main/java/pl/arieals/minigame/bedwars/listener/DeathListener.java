package pl.arieals.minigame.bedwars.listener;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.util.Vector;

import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;
import pl.arieals.minigame.bedwars.arena.RevivePlayerCountdown;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class DeathListener implements Listener
{
    @Inject
    private BukkitApiCore apiCore;

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event)
    {
        event.setDeathMessage(null); // my wcale nie umieramy!
        final Player player = event.getEntity();

        final BedWarsPlayer playerData = getPlayerData(player, BedWarsPlayer.class);
        if (playerData.getTeam() == null)
        {
            return;
        }

        player.setHealth(20);
        player.setVisible(false);
        player.setAllowFlight(true);
        player.setFlying(true);

        final Vector direction = player.getLocation().getDirection();
        final Vector newVector = direction.multiply(- 1).setY(2);
        player.setVelocity(newVector);

        playerData.setAlive(false);
        if (playerData.getTeam().isBedAlive())
        {
            new RevivePlayerCountdown(player, playerData).start(20);
        }
        else
        {
            player.sendMessage("Twoj team nie ma lozka, zdychaj");
            // todo jakis komunikat o braku lozka
        }
    }
}
