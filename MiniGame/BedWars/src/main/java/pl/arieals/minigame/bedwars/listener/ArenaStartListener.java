package pl.arieals.minigame.bedwars.listener;

import javax.xml.bind.JAXB;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameStartEvent;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.LobbyInitEvent;
import pl.arieals.minigame.bedwars.arena.BedDestroyTask;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;
import pl.arieals.minigame.bedwars.arena.generator.GeneratorTask;
import pl.arieals.minigame.bedwars.cfg.BedWarsArenaConfig;
import pl.arieals.minigame.bedwars.cfg.BedWarsConfig;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class ArenaStartListener implements Listener
{
    @Inject
    private BedWarsConfig config;

    @EventHandler
    public void onLobbyInit(final LobbyInitEvent event)
    {
        final LocalArena arena = event.getArena();

        final BedWarsArenaConfig config = JAXB.unmarshal(arena.getWorld().getResource("BedWarsArena.xml"), BedWarsArenaConfig.class);
        arena.setArenaData(new BedWarsArena(arena, config));
    }

    @EventHandler
    public void onGameStart(final GameStartEvent event)
    {
        final LocalArena arena = event.getArena();

        // planujemy task generatorów co 1 tick
        arena.getScheduler().runTaskTimer(new GeneratorTask(arena), 1, 1);

        // planujemy task usuwajacy lozka
        arena.getScheduler().runTaskLater(new BedDestroyTask(arena), this.config.getDestroyBedsAt());
    }
}
