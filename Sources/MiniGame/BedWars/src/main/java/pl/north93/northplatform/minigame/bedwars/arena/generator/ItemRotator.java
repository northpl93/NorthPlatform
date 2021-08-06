package pl.north93.northplatform.minigame.bedwars.arena.generator;

import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getArenas;


import net.minecraft.server.v1_12_R1.MinecraftServer;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.shared.api.GamePhase;
import pl.north93.northplatform.minigame.bedwars.arena.BedWarsArena;

/**
 * Klasa uzywana do obracania itemków
 * Musimy to robic czesciej niz tick, dlatego uzywamy Thread
 */
@Slf4j
public final class ItemRotator extends Thread
{
    @Bean
    private ItemRotator()
    {
        super("BedWars-ItemRotator");
        log.info("Starting ItemRotator thread");
        this.start();
    }

    @Override
    public synchronized void run() // mark everything as synchronized to make wait() happy
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
