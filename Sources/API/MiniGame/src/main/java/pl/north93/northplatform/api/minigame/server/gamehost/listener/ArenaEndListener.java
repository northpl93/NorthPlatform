package pl.north93.northplatform.api.minigame.server.gamehost.listener;

import static org.bukkit.event.EventPriority.MONITOR;


import java.time.Duration;
import java.util.Set;

import org.bukkit.event.EventHandler;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.gamephase.GameEndEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.player.PlayerQuitArenaEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.player.SpectatorQuitEvent;
import pl.north93.northplatform.api.minigame.shared.api.GamePhase;
import pl.north93.northplatform.api.minigame.shared.api.match.IMatchAccess;
import pl.north93.northplatform.api.minigame.shared.api.match.StandardMatchStatistics;
import pl.north93.northplatform.api.minigame.shared.api.statistics.unit.DurationUnit;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.server.IBukkitExecutor;
import pl.north93.northplatform.api.bukkit.utils.AutoListener;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

@Slf4j
public class ArenaEndListener implements AutoListener
{
    @Inject
    private IBukkitExecutor bukkitExecutor;

    @EventHandler(priority = MONITOR)
    public void stopEmptyArenaWhenPlayerQuit(final PlayerQuitArenaEvent event)
    {
        this.checkArenaShouldEnd(event.getArena());
    }

    @EventHandler(priority = MONITOR)
    public void stopEmptyArenaWhenSpectatorQuit(final SpectatorQuitEvent event)
    {
        this.checkArenaShouldEnd(event.getArena());
    }

    private void checkArenaShouldEnd(final LocalArena arena)
    {
        final Set<INorthPlayer> allPlayers = arena.getPlayersManager().getAllPlayers();
        if (! allPlayers.isEmpty())
        {
            return;
        }

        final GamePhase gamePhase = arena.getGamePhase();
        if (gamePhase == GamePhase.INITIALISING || gamePhase == GamePhase.LOBBY)
        {
            // W initialising nic nie robimy zeby nie zbugowac
            // W lobby nie trzeba nic robic
            return;
        }

        // jak arena byla w trakcie gry lub po grze to przelaczamy do ponownej inicjalizacji
        log.info("Arena {} is empty, switching to INITIALISING...", arena.getId());
        this.bukkitExecutor.sync(() ->
        {
            if (gamePhase == GamePhase.STARTED)
            {
                arena.endGame();
            }
            else if (gamePhase == GamePhase.POST_GAME)
            {
                // nie wywolujemy prepareNewCycle() bo ono odpowiada tylko za rozlaczenie graczy
                arena.setGamePhase(GamePhase.INITIALISING);
            }
        });
    }

    @EventHandler
    public void markMatchAsEnded(final GameEndEvent event)
    {
        final IMatchAccess match = event.getArena().getMatch();
        if (match == null)
        {
            log.warn("Match is null on arena {} when switched to post_game", event.getArena().getId());
            return;
        }

        // operacje na bazie danych mongo
        this.bukkitExecutor.async(() ->
        {
            // oznaczamy mecz jako zakonczony
            match.endMatch();

            final Duration matchDuration = Duration.between(match.getStartedAt(), match.getEndedAt());
            match.getStatistics().record(StandardMatchStatistics.MATCH_DURATION, new DurationUnit(matchDuration));

            log.info("Match {} on arena {} finished with duration {}", match.getMatchId(), match.getArenaId(), matchDuration);
        });
    }

    @EventHandler
    public void switchToInitialisingWhenEndEmpty(final GameEndEvent event)
    {
        final LocalArena arena = event.getArena();
        if (! arena.getPlayersManager().getPlayers().isEmpty())
        {
            return;
        }

        log.info("Arena {} has been switched to POST_GAME without players, switching to INITIALISING", arena.getId());
        this.bukkitExecutor.sync(() ->
        {
            // kiedy cos przelaczylo pusta arene do POST_GAME to natychmiast przerzucamy do INITIALISING
            // dopiero przy nastepnym ticku zeby nie zepsuc innej logiki w minigrach
            arena.setGamePhase(GamePhase.INITIALISING);
        });
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
