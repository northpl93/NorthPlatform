package pl.arieals.minigame.bedwars.listener;

import javax.xml.bind.JAXB;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameStartEvent;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.LobbyInitEvent;
import pl.arieals.minigame.bedwars.arena.BedDestroyTask;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;
import pl.arieals.minigame.bedwars.arena.generator.GeneratorTask;
import pl.arieals.minigame.bedwars.cfg.BwArenaConfig;
import pl.arieals.minigame.bedwars.cfg.BwConfig;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class ArenaStartListener implements Listener
{
    @Inject
    private BwConfig config;

    @EventHandler
    public void onLobbyInit(final LobbyInitEvent event)
    {
        final LocalArena arena = event.getArena();

        final BwArenaConfig config = JAXB.unmarshal(arena.getWorld().getResource("BedWarsArena.xml"), BwArenaConfig.class);
        arena.setArenaData(new BedWarsArena(arena, this.config, config));
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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
