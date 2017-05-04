package pl.arieals.minigame.elytrarace.listener;

import javax.xml.bind.JAXB;

import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameStartedEvent;
import pl.arieals.api.minigame.server.gamehost.utils.xml.XmlLocation;
import pl.arieals.minigame.elytrarace.arena.ElytraRaceArena;
import pl.arieals.minigame.elytrarace.arena.StartCountdown;
import pl.arieals.minigame.elytrarace.cfg.ArenaConfig;
import pl.north93.zgame.api.bukkit.BukkitApiCore;

public class ArenaStartListener implements Listener
{
    private BukkitApiCore apiCore;

    @EventHandler
    public void startGame(final GameStartedEvent event)
    {
        final ElytraRaceArena arenaData = new ElytraRaceArena(this.loadConfig(event.getArena()));
        event.getArena().setArenaData(arenaData);

        this.setupPlayers(event.getArena(), arenaData);

        // task odpalający arenę po 10 sekundach
        new StartCountdown(10, event.getArena()).runTaskTimer(this.apiCore.getPluginMain(), 20, 20);
    }

    private ArenaConfig loadConfig(final LocalArena arena)
    {
        return JAXB.unmarshal(arena.getWorld().getResource("ElytraRaceArena.xml"), ArenaConfig.class);
    }

    private void setupPlayers(final LocalArena arena, final ElytraRaceArena elytraRaceArena)
    {
        final Iterator<XmlLocation> locations = elytraRaceArena.getArenaConfig().getStartLocations().iterator();

        for (final Player player : arena.getPlayersManager().getPlayers())
        {
            player.teleport(locations.next().toBukkit(arena.getWorld().getWorld()));

            player.getInventory().setChestplate(this.createElytra());

            player.setAllowFlight(true);
            player.setFlying(true);
        }
    }

    private ItemStack createElytra()
    {
        final ItemStack elytra = new ItemStack(Material.ELYTRA);
        final ItemMeta itemMeta = elytra.getItemMeta();
        itemMeta.spigot().setUnbreakable(true);
        elytra.setItemMeta(itemMeta);
        return elytra;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
