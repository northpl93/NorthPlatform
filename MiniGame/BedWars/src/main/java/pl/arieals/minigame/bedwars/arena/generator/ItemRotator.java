package pl.arieals.minigame.bedwars.arena.generator;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArenas;


import net.minecraft.server.v1_10_R1.MinecraftServer;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;

/**
 * Klasa uzywana do obracania itemków
 * Musimy to robic czesciej niz tick, dlatego uzywamy Thread
 */
public final class ItemRotator extends Thread
{
    public ItemRotator()
    {
        super("BedWars-ItemRotator");
    }

    @Override
    public synchronized void run() // mark everything as synchronized
    {
        while (MinecraftServer.getServer().isRunning())
        {
            for (final LocalArena arena : getArenas())
            {
                final BedWarsArena arenaData = arena.getArenaData();
                if (arena.getGamePhase() != GamePhase.STARTED || arenaData == null)
                {
                    continue; // arena nie jest teraz uruchomiona wiec nic nie spawnimy
                }
                for (final GeneratorController generatorController : arenaData.getGenerators())
                {
                    generatorController.getHudHandler().handleItemRotation();
                }
            }

            try
            {
                this.wait(10);
            }
            catch (final InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
