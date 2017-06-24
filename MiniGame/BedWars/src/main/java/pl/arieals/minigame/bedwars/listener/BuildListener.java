package pl.arieals.minigame.bedwars.listener;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;


import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;
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
            event.getPlayer().sendMessage("Nie mozesz tu niszczyc");
            event.setCancelled(true);
            return;
        }

        if (arenaData.getPlayerBlocks().remove(event.getBlock()))
        {
            return; // ok, block byl na liscie i go usunelismy
        }

        event.setCancelled(true);
        event.getPlayer().sendMessage("Mozesz niszczyc tylko bloki ktore postawiles!");
    }

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
