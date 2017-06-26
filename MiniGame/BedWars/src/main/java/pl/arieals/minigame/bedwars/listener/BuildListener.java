package pl.arieals.minigame.bedwars.listener;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;
import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;
import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.north93.zgame.api.bukkit.utils.region.Cuboid;

public class BuildListener implements Listener
{
    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event)
    {
        final LocalArena arena = getArena(event.getBlock().getWorld());
        final BedWarsArena arenaData = arena.getArenaData();

        if (arena.getGamePhase() != GamePhase.STARTED)
        {
            // gdy gra nie wystartowala to nic nie mozna robic
            event.setCancelled(true);
            return;
        }

        if (this.checkSecureRegion(arenaData, event.getBlock()))
        {
            event.getPlayer().sendMessage("Nie mozesz tu budowac");
            event.setCancelled(true);
            return;
        }

        arenaData.getPlayerBlocks().add(event.getBlock());
    }

    @EventHandler
    private void onBlockDestroy(final BlockBreakEvent event)
    {
        final Block block = event.getBlock();
        final LocalArena arena = getArena(block.getWorld());
        final BedWarsArena arenaData = arena.getArenaData();

        if (arena.getGamePhase() != GamePhase.STARTED)
        {
            // gdy gra nie wystartowala to nic nie mozna robic
            event.setCancelled(true);
            return;
        }

        if (this.checkSecureRegion(arenaData, block))
        {
            event.getPlayer().sendMessage("Nie mozesz tu niszczyc");
            event.setCancelled(true);
            return;
        }

        if (this.handleBedBreak(event, arenaData))
        {
            return;
        }

        if (arenaData.getPlayerBlocks().remove(block))
        {
            return; // ok, block byl na liscie i go usunelismy
        }

        event.setCancelled(true);
        event.getPlayer().sendMessage("Mozesz niszczyc tylko bloki ktore postawiles!");
    }

    // zwraca true jesli gracz niszczy lozko - wtedy nie wykonujemy reszty kodu w evencie
    private boolean handleBedBreak(final BlockBreakEvent event, final BedWarsArena arenaData)
    {
        final Block block = event.getBlock();
        if (block.getType() != Material.BED_BLOCK)
        {
            return false; // nie lozko - nie returnujemy reszty kodu w evencie
        }

        final Team teamAt = arenaData.getTeamAt(block);
        final BedWarsPlayer playerData = getPlayerData(event.getPlayer(), BedWarsPlayer.class);

        if (teamAt == playerData.getTeam())
        {
            event.setCancelled(true); // gracz nie moze zniszczyc lozka swojej druzyny
            return true;
        }

        teamAt.setBedAlive(false); // oznaczamy, ze lozko druzyny zostalo zniszczone

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
}
