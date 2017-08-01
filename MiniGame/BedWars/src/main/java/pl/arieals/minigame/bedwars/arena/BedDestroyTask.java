package pl.arieals.minigame.bedwars.arena;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.arena.DeathMatchState;
import pl.arieals.minigame.bedwars.event.BedDestroyedEvent;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class BedDestroyTask extends BukkitRunnable
{
    @Inject
    private BukkitApiCore apiCore;
    private final LocalArena arena;

    public BedDestroyTask(final LocalArena arena)
    {
        this.arena = arena;
    }

    @Override
    public void run()
    {
        if (this.arena.getDeathMatch().getState() != DeathMatchState.NOT_STARTED)
        {
            // jesli deatmatch wystartowal to nic nie robimy
            return;
        }

        final BedWarsArena arenaData = this.arena.getArenaData();
        for (final Team team : arenaData.getTeams())
        {
            if (! team.isBedAlive())
            {
                continue;
            }

            team.setBedAlive(false);
            final Block bedBlock = team.getBedLocation().getBlock();
            // usuwamy klasyczna, wolna metoda zeby znikly obydwie czesci lozka.
            bedBlock.setType(Material.AIR);

            this.apiCore.callEvent(new BedDestroyedEvent(this.arena, null, bedBlock, team));
        }
    }
}
