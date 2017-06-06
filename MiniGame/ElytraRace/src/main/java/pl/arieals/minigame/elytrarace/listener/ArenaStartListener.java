package pl.arieals.minigame.elytrarace.listener;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.setPlayerData;


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
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameStartEvent;
import pl.arieals.minigame.elytrarace.ElytraRaceMode;
import pl.arieals.minigame.elytrarace.arena.ElytraRaceArena;
import pl.arieals.minigame.elytrarace.arena.ElytraRacePlayer;
import pl.arieals.minigame.elytrarace.arena.ElytraScorePlayer;
import pl.arieals.minigame.elytrarace.arena.StartCountdown;
import pl.arieals.minigame.elytrarace.cfg.ArenaConfig;
import pl.north93.zgame.api.bukkit.utils.xml.XmlLocation;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class ArenaStartListener implements Listener
{
    @Inject @Messages("ElytraRace")
    private MessagesBox messages;

    @EventHandler
    public void startGame(final GameStartEvent event)
    {
        final ElytraRaceArena arenaData = new ElytraRaceArena(this.loadConfig(event.getArena()), ElytraRaceMode.SCORE_MODE);
        event.getArena().setArenaData(arenaData);

        this.setupPlayers(event.getArena(), arenaData);

        event.getArena().getPlayersManager().broadcast(this.messages, arenaData.getGameMode() == ElytraRaceMode.SCORE_MODE ? "score.welcome" : "race.welcome");

        // task odpalający arenę po 15 sekundach
        new StartCountdown(15, event.getArena()).start(20);
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
            setPlayerData(player, new ElytraRacePlayer());
            if (elytraRaceArena.getGameMode() == ElytraRaceMode.SCORE_MODE)
            {
                // w trybie score ustawiamy graczowi dodatkowy obiekt
                // sledzacy ilosc punktów, combo itp.
                setPlayerData(player, new ElytraScorePlayer());
            }

            player.teleport(locations.next().toBukkit(arena.getWorld().getCurrentWorld()));
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
