package pl.north93.zgame.antycheat.timeline.impl;

import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import pl.north93.zgame.antycheat.analysis.impl.AnalysisManager;
import pl.north93.zgame.antycheat.timeline.Tick;
import pl.north93.zgame.antycheat.timeline.TimelineEvent;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class TimelineManager
{
    @Inject
    private TickManager     tickManager;
    @Inject
    private TickController  tickController;
    @Inject
    private AnalysisManager analysisManager;
    @Inject
    private BukkitApiCore   bukkitApiCore;

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
        player.setMetadata("AntyCheat.Timeline", new FixedMetadataValue(this.bukkitApiCore.getPluginMain(), timeline));
        return timeline;
    }

    public void pushEventForPlayer(final Player player, final TimelineEvent event)
    {
        this.getPlayerTimeline(player).pushEvent(event);
    }

    public void forEachTimeline(final Consumer<TimelineImpl> timelineConsumer)
    {
        for (final Player player : Bukkit.getOnlinePlayers())
        {
            timelineConsumer.accept(this.getPlayerTimeline(player));
        }
    }

    private final class TickHandlerImpl implements TickHandler
    {
        @Override
        public void tickBegin()
        {
            TimelineManager.this.preTick();
        }

        @Override
        public void tickEnd()
        {
            TimelineManager.this.postTick();
        }
    }
}
