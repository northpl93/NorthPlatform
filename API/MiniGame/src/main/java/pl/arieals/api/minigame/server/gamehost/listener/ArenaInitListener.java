package pl.arieals.api.minigame.server.gamehost.listener;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.utils.math.DioriteRandomUtils;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameInitEvent;
import pl.arieals.api.minigame.server.gamehost.region.ITrackedRegion;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.api.minigame.shared.api.LobbyMode;
import pl.arieals.api.minigame.shared.api.MapTemplate;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class ArenaInitListener implements Listener
{
    @Inject
    private MiniGameServer server;
    @Inject
    private Logger         logger;

    @EventHandler(priority = EventPriority.LOW) // before normal
    public void onArenaInit(final GameInitEvent event)
    {
        final GameHostManager hostManager = this.server.getServerManager();
        final LocalArena arena = event.getArena();

        this.logger.info("Minigames API is initialising arena " + arena.getId());

        if (arena.isDynamic())
        {
            // przenosimy wszystkich graczy do lobby areny jesli gra jest dynamiczna
            //for (final Player player : arena.getPlayersManager().getPlayers())
            //{
            //    hostManager.getLobbyManager().addPlayer(arena, player);
            //} Tym powinna zajac sie minigra?
        }
        else
        {
            final ArrayList<Player> players = new ArrayList<>(arena.getPlayersManager().getPlayers());
            players.forEach(player -> player.kickPlayer("Powinienes wyleciec do poczekalni serwera, ale // TODO"));
            Bukkit.broadcastMessage("Now kick all players to server lobby");
            // todo kick all players to server lobby
        }

        // usuwamy wszystkie regiony pozostale po poprzedniej grze
        hostManager.getRegionManager().getRegions(arena.getWorld().getCurrentWorld()).forEach(ITrackedRegion::unTrack);

        // usuwamy arena data zeby nowa gra miala czyste srodowisko pracy
        arena.setArenaData(null);

        // resetujemy stan death matchu.
        arena.getDeathMatch().resetState();

        if (arena.getLobbyMode() == LobbyMode.INTEGRATED)
        {
            // jesli lobby jest zintegrowane z mapa to glosowanie na pewno jest wylaczone
            // i musimy juz teraz zaladowac nowa losowa mape.
            // Po zakonczeniu arena bedzie przelaczona w LOBBY.
            final MapTemplate map = DioriteRandomUtils.getRandom(hostManager.getMapTemplateManager().getAllTemplates());
            arena.getWorld().setActiveMap(map).onComplete(() -> arena.setGamePhase(GamePhase.LOBBY));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR) // wykonuje sie na samym koncu
    public void postArenaInit(final GameInitEvent event)
    {
        final LocalArena arena = event.getArena();
        if (arena.getLobbyMode() == LobbyMode.EXTERNAL)
        {
            // na arenie z lobby zewnętrznym możemy bez czekania przełączyć arenę w tryb LOBBY
            arena.setGamePhase(GamePhase.LOBBY);
        }
    }
    
    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
