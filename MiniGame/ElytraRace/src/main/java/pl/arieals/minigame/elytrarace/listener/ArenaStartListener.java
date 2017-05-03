package pl.arieals.minigame.elytrarace.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameStartedEvent;
import pl.arieals.minigame.elytrarace.arena.ElytraRaceArena;
import pl.arieals.minigame.elytrarace.arena.StartCountdown;
import pl.north93.zgame.api.bukkit.BukkitApiCore;

public class ArenaStartListener implements Listener
{
    private BukkitApiCore apiCore;

    @EventHandler
    public void startGame(final GameStartedEvent event)
    {
        event.getArena().setArenaData(new ElytraRaceArena());
        for (final Player player : event.getArena().getPlayersManager().getPlayers())
        {
            player.teleport(new Location(event.getArena().getWorld().getWorld(), 0, 201, 0)); // todo
            player.getInventory().setChestplate(new ItemStack(Material.ELYTRA));

            player.setAllowFlight(true);
            player.setFlying(true);
        }

        // task odpalający arenę po 10 sekundach
        new StartCountdown(10, event.getArena()).runTaskTimer(this.apiCore.getPluginMain(), 20, 20);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
