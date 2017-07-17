package pl.arieals.minigame.bedwars.listener;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;
import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;
import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.arieals.minigame.bedwars.event.BedDestroyedEvent;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.utils.region.Cuboid;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class BuildListener implements Listener
{
    @Inject
    private BukkitApiCore apiCore;
    @Inject @Messages("BedWars")
    private MessagesBox   messages;

    @EventHandler
    public void onCrafting(final CraftItemEvent event) // blokujemy wszelki crafting zeby sami nie zrobili sobie blokow z irona
    {
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event)
    {
        final LocalArena arena = getArena(event.getBlock().getWorld());
        final BedWarsArena arenaData = arena.getArenaData();
        final BedWarsPlayer playerData = getPlayerData(event.getPlayer(), BedWarsPlayer.class);

        if (arena.getGamePhase() != GamePhase.STARTED)
        {
            // gdy gra nie wystartowala to nic nie mozna robic
            event.setCancelled(true);
            return;
        }

        if (playerData == null || ! playerData.isAlive())
        {
            // gdy gracz nie zyje lub jest spectatorem/innym intruzem anulujemy
            event.setCancelled(true);
            return;
        }

        if (this.checkSecureRegion(arenaData, event.getBlock()))
        {
            this.messages.sendMessage(event.getPlayer(), "no_permissions");
            event.setCancelled(true);
            return;
        }

        arenaData.getPlayerBlocks().add(event.getBlock());
    }

    @EventHandler(ignoreCancelled = true)
    private void onBlockDestroy(final BlockBreakEvent event)
    {
        final Block block = event.getBlock();
        final LocalArena arena = getArena(block.getWorld());
        final BedWarsArena arenaData = arena.getArenaData();
        final BedWarsPlayer playerData = getPlayerData(event.getPlayer(), BedWarsPlayer.class);

        if (arena.getGamePhase() != GamePhase.STARTED)
        {
            // gdy gra nie wystartowala to nic nie mozna robic
            event.setCancelled(true);
            return;
        }

        if (playerData == null || ! playerData.isAlive())
        {
            // gdy gracz nie zyje lub jest spectatorem/innym intruzem anulujemy
            event.setCancelled(true);
            return;
        }

        if (this.checkSecureRegion(arenaData, block))
        {
            this.messages.sendMessage(event.getPlayer(), "no_permissions");
            event.setCancelled(true);
            return;
        }

        if (this.handleBedBreak(event, arenaData, playerData))
        {
            return;
        }

        if (arenaData.getPlayerBlocks().remove(block))
        {
            return; // ok, block byl na liscie i go usunelismy
        }

        event.setCancelled(true);
        this.messages.sendMessage(event.getPlayer(), "no_permissions");
    }

    // zwraca true jesli gracz niszczy lozko - wtedy nie wykonujemy reszty kodu w evencie
    private boolean handleBedBreak(final BlockBreakEvent event, final BedWarsArena arenaData, final BedWarsPlayer playerData)
    {
        final Block block = event.getBlock();
        if (block.getType() != Material.BED_BLOCK)
        {
            return false; // nie lozko - nie returnujemy reszty kodu w evencie
        }

        final Team teamAt = arenaData.getTeamAt(block);
        if (teamAt == playerData.getTeam())
        {
            event.setCancelled(true); // gracz nie moze zniszczyc lozka swojej druzyny
            return true;
        }
        if (! teamAt.isBedAlive())
        {
            return true; // lozko mozna zniszczyc tylko raz, zabezpieczenie przed bugami Bukkita
        }
        teamAt.setBedAlive(false); // oznaczamy, ze lozko druzyny zostalo zniszczone

        // wywolujemy event zniszczenia lozka
        this.apiCore.callEvent(new BedDestroyedEvent(arenaData.getArena(), playerData.getBukkitPlayer(), block, teamAt));

        return true;
    }

    // zwraca true jesli blok jest w strefie niemodyfikowalnej
    private boolean checkSecureRegion(final BedWarsArena arenaData, final Block block)
    {
        for (final Cuboid cuboid : arenaData.getSecureRegions())
        {
            if (cuboid.contains(block))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
