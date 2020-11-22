package pl.north93.northplatform.antycheat.timeline.impl;

import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

import co.aikar.timings.Timing;
import pl.north93.northplatform.antycheat.analysis.SingleAnalysisResult;
import pl.north93.northplatform.antycheat.analysis.impl.AnalysisManager;
import pl.north93.northplatform.antycheat.analysis.impl.ViolationsStorage;
import pl.north93.northplatform.antycheat.timeline.Tick;
import pl.north93.northplatform.antycheat.timeline.TimelineEvent;
import pl.north93.northplatform.antycheat.utils.AntyCheatTimings;
import pl.north93.northplatform.api.bukkit.server.IBukkitServerManager;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

public class TimelineManager
{
    @Inject
    private TickManager tickManager;
    @Inject
    private TickController tickController;
    @Inject
    private AnalysisManager analysisManager;
    @Inject
    private IBukkitServerManager serverManager;

    @Bean
    private TimelineManager()
    {
        this.tickController.addTickHandler(new TickHandlerImpl());
    }

    private void preTick()
    {
        // rozpoczynamy tick we wszystkich liniach czasu
        this.forEachTimeline(TimelineImpl::tickBegin);
    }

    private void postTick()
    {
        final TickImpl currentTick = this.tickManager.getCurrentTick();

        // oznaczamy aktualny tick jako zakonczony
        currentTick.makeCompleted();

        this.forEachTimeline(timeline ->
        {
            // rejestrujemy statystyki ticku.
            timeline.endTick();

            // uruchamiamy analizę linii czasu
            this.analysisManager.fireAnalysis(timeline, currentTick);

            // podsumowanie ticku i wywolanie listenerów zmiany poziomu gracza
            this.analysisManager.summarizeTick(timeline.getOwner(), currentTick);
        });
    }

    /**
     * Zwraca aktualnie trwający tick.
     *
     * @return Aktualnie trwający tick.
     */
    public TickImpl getCurrentTick() // aktualnie trwajacy tick
    {
        return this.tickManager.getCurrentTick();
    }

    /**
     * Zwraca tick o X wcześniejszy od podanego.
     *
     * @param tick Tick od którego liczymy wcześniejszy tick.
     * @param previous O ile wcześniejszy tick.
     * @return Tick o X wcześniejszy od podanego.
     */
    public TickImpl getPreviousTick(final Tick tick, final int previous)
    {
        if (previous == 0)
        {
            return (TickImpl) tick; // maly quality of life improvement
        }
        return this.tickManager.getTick(tick.getTickId() - previous);
    }

    public TickImpl getNextTick(final Tick tick)
    {
        final TickImpl currentTick = this.getCurrentTick();
        final int currentTickId = currentTick.getTickId();
        final int tickToGet = tick.getTickId() + 1;

        if (tickToGet > currentTickId)
        {
            return null;
        }
        else if (tickToGet == currentTickId)
        {
            return currentTick;
        }

        return this.tickManager.getTick(tickToGet);
    }

    /**
     * Zwraca linię wydarzeń powiązaną z danym graczem.
     *
     * @param player Gracz dla którego pobieramy linię czasu.
     * @return Linia czasu gracza
     */
    public TimelineImpl getPlayerTimeline(final Player player)
    {
        final List<MetadataValue> metadata = player.getMetadata("AntyCheat.Timeline");
        if (! metadata.isEmpty())
        {
            return (TimelineImpl) metadata.get(0).value();
        }

        final TimelineImpl timeline = new TimelineImpl(this, player, 20 * 10);
        player.setMetadata("AntyCheat.Timeline", this.serverManager.createFixedMetadataValue(timeline));
        return timeline;
    }

    /**
     * Zwraca obiekt przechowujący naruszenia danego gracza.
     *
     * @param player Gracz dla którego pobieramy ViolationsStorage.
     * @return Obiekt przechowujący naruszenia danego gracza.
     */
    public ViolationsStorage getViolations(final Player player)
    {
        return this.getPlayerTimeline(player).getViolations();
    }

    public void pushEventForPlayer(final Player player, final TimelineEvent event)
    {
        this.getPlayerTimeline(player).pushEvent(event);
    }

    // dodaje dane naruszenia do historii w ViolationsStorage
    public void pushAnalysisResultForPlayer(final Player player, final Tick tick, final SingleAnalysisResult analysisResult)
    {
        final TimelineImpl timeline = this.getPlayerTimeline(player);
        timeline.getViolations().recordAnalysisResult(tick, analysisResult);
    }

    public void forEachTimeline(final Consumer<TimelineImpl> timelineConsumer)
    {
        for (final Player player : Bukkit.getOnlinePlayers())
        {
            timelineConsumer.accept(this.getPlayerTimeline(player));
        }
    }

    public AnalysisManager getAnalysisManager()
    {
        return this.analysisManager;
    }

    private final class TickHandlerImpl implements TickHandler
    {
        @Override
        public void tickBegin()
        {
            try (final Timing timing = AntyCheatTimings.PRE_TICK.startTiming())
            {
                TimelineManager.this.preTick();
            }
        }

        @Override
        public void tickEnd()
        {
            try (final Timing timing = AntyCheatTimings.POST_TICK.startTiming())
            {
                TimelineManager.this.postTick();
            }
        }
    }
}
