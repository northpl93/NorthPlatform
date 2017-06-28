package pl.arieals.minigame.bedwars.arena.generator;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArenas;


import org.bukkit.scheduler.BukkitRunnable;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;

public final class GeneratorTask extends BukkitRunnable
{
    @Override
    public void run()
    {
        for (final LocalArena arena : getArenas())
        {
            final BedWarsArena arenaData = arena.getArenaData();
            if (arena.getGamePhase() != GamePhase.STARTED || arenaData == null)
            {
                continue; // arena nie jest teraz uruchomiona wiec nic nie spawnimy
            }
            arenaData.getGenerators().forEach(GeneratorController::tick);
        }
    }
}
