package pl.north93.northplatform.api.minigame.server.gamehost.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.commons.math.DioriteRandomUtils;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.bukkit.server.IBukkitServerManager;
import pl.north93.northplatform.api.bukkit.server.AutoListener;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.minigame.server.gamehost.GameHostManager;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.MapSwitchedEvent.MapSwitchReason;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.gamephase.GameInitEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.region.ITrackedRegion;
import pl.north93.northplatform.api.minigame.shared.api.GamePhase;
import pl.north93.northplatform.api.minigame.shared.api.LobbyMode;
import pl.north93.northplatform.api.minigame.shared.api.MapTemplate;

@Slf4j
public class ArenaInitListener implements AutoListener
{
    @Inject
    private GameHostManager gameHostManager;
    @Inject
    private IBukkitServerManager bukkitServerManager;

    @EventHandler(priority = EventPriority.LOWEST)
    public void beforeInit(final GameInitEvent event)
    {
        final LocalArena arena = event.getArena();

        if (! arena.isDynamic() && ! arena.getPlayersManager().getPlayers().isEmpty())
        {
            throw new IllegalStateException("Non-dynamic game switched to INITIALISING with players on it.");
        }

        if (this.bukkitServerManager.isShutdownScheduled())
        {
            log.info("Removing arena {} because shutdown is scheduled.", arena.getId());
            event.setCancelled(true);
            arena.delete();
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true) // before normal
    public void onArenaInit(final GameInitEvent event)
    {
        final LocalArena arena = event.getArena();

        log.info("Minigames API is initialising arena {}", arena.getId());

        // resetujemy licznik, aby przy kazdej inicjalizacji wskazywal 0
        arena.getTimer().reset();

        // usuwamy wszystkie regiony pozostale po poprzedniej grze
        this.gameHostManager.getRegionManager().getRegions(arena.getWorld().getCurrentWorld()).forEach(ITrackedRegion::unTrack);

        // resetujemy stan death matchu.
        arena.getDeathMatch().resetState();

        // resetujemy obiekt meczu po ewentualnie zakonczonej grze
        arena.setMatch(null);

        // resetuje liste nagrod
        arena.getRewards().reset();

        if (arena.getLobbyMode() == LobbyMode.INTEGRATED)
        {
            // usuwamy arena data zeby nowa gra miala czyste srodowisko pracy
            arena.setArenaData(null);
            
            // jesli lobby jest zintegrowane z mapa to glosowanie na pewno jest wylaczone
            // i musimy juz teraz zaladowac nowa losowa mape.
            // Po zakonczeniu arena bedzie przelaczona w LOBBY.
            final MapTemplate map = DioriteRandomUtils.getRandom(this.gameHostManager.getMapTemplateManager().getAllTemplates());
            arena.getWorld().setActiveMap(map, MapSwitchReason.ARENA_INITIALISE).onComplete(() -> arena.setGamePhase(GamePhase.LOBBY));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true) // wykonuje sie na samym koncu
    public void postArenaInit(final GameInitEvent event)
    {
        final LocalArena arena = event.getArena();
        if (arena.getLobbyMode() == LobbyMode.EXTERNAL)
        {
            // na arenie z lobby zewnętrznym możemy bez czekania przełączyć arenę w tryb LOBBY
            // na arenie z lobby wbudowanym czekamy, aż się załaduje mapa (wyżej onComplete)
            arena.setGamePhase(GamePhase.LOBBY);
        }
    }
    
    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
