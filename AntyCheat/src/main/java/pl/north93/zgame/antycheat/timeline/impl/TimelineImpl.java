package pl.north93.zgame.antycheat.timeline.impl;

import static org.diorite.utils.SimpleEnum.SMALL_LOAD_FACTOR;


import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.antycheat.analysis.timeline.TimelineAnalyserConfig;
import pl.north93.zgame.antycheat.event.impl.PlayerSpawnTimelineEvent;
import pl.north93.zgame.antycheat.timeline.PlayerTickInfo;
import pl.north93.zgame.antycheat.timeline.Tick;
import pl.north93.zgame.antycheat.timeline.Timeline;
import pl.north93.zgame.antycheat.timeline.TimelineEvent;
import pl.north93.zgame.antycheat.timeline.TimelineWalker;

/*default*/ class TimelineImpl implements Timeline
{
    private final TimelineManager timelineManager;
    private final PlayerDataImpl  data;
    private final int             ticksToTrack;
    private final List<TimelineEvent>       queuedEvents;
    private final LinkedEventsQueue         events;
    private final Map<Tick, PlayerTickInfo> tickInfo;

    public TimelineImpl(final TimelineManager timelineManager, final Player player, final int ticksToTrack)
    {
        this.timelineManager = timelineManager;
        this.data = new PlayerDataImpl(player);
        this.ticksToTrack = ticksToTrack;

        this.queuedEvents = new LinkedList<>();
        this.events = new LinkedEventsQueue(timelineManager);
        this.tickInfo = new HashMap<>(ticksToTrack, SMALL_LOAD_FACTOR);
    }

    /**
     * Zwraca dane przypisane do danego gracza przez checki.
     *
     * @return Obiekt przechowujący dane gracza.
     */
    @Override
    public PlayerDataImpl getData()
    {
        return this.data;
    }

    @Override
    public int getTrackedTicks()
    {
        // ta mapa zawiera ostatnie X tickow ktore sa sledzone przez nasza linie czasu
        return this.tickInfo.size();
    }

    @Override
    public PlayerTickInfo getCurrentPlayerTickInfo()
    {
        return this.getPlayerTickInfo(this.getCurrentTick());
    }

    @Override
    public PlayerTickInfo getPlayerTickInfo(final Tick tick)
    {
        return this.tickInfo.get(tick);
    }

    @Override
    public Collection<TimelineEvent> getEvents() // najlepiej cachowac ta metode bo nie jest zbyt szybka
    {
        return this.events.getEvents();
    }

    @Override
    public Collection<TimelineEvent> getEvents(final Tick tick) // najlepiej cachowac ta metode bo nie jest zbyt szybka
    {
        return this.events.getEventsInTick(tick);
    }

    @Override
    public TimelineWalker createWalkerForScope(final TimelineAnalyserConfig.Scope scope)
    {
        final Tick currentTick = this.getCurrentTick();
        switch (scope)
        {
            case TICK:
                return this.events.createWalkerInTickRangeWithCursorAtEnd(currentTick, currentTick);
            case SECOND:
                return this.createWalkerForTicks(currentTick, 20);
            case ALL:
                return this.events.createWalkerInFullRange(true);
        }
        return null;
    }

    private TimelineWalker createWalkerForTicks(final Tick lastTick, final int ticks)
    {
        final TickImpl firstTick = this.timelineManager.getPreviousTick(lastTick, Math.min(ticks - 1, this.getTrackedTicks()));
        return this.events.createWalkerInTickRangeWithCursorAtEnd(firstTick, lastTick);
    }

    /**
     * Dodaje event do tej linii czasu do aktualnie trwajacego ticku,
     * jesli tick jest oznaczony jako zakonczony to zostanie przypisany do nastepnego.
     *
     * @param timelineEvent Wydarzenie do dodania na linie czasu.
     */
    public synchronized void pushEvent(final TimelineEvent timelineEvent)
    {
        //Bukkit.broadcastMessage(timelineEvent.toString()); // todo
        final Tick currentTick = this.getCurrentTick();
        if (currentTick.isCompleted())
        {
            this.queuedEvents.add(timelineEvent);
        }
        else
        {
            this.events.putEvent(currentTick, timelineEvent);
        }
    }

    /**
     * Metoda przygotowujaca linie czasu do nastepnego ticku.
     */
    public synchronized void tickBegin()
    {
        final Tick currentTick = this.getCurrentTick();

        for (final TimelineEvent queuedEvent : this.queuedEvents)
        {
            this.events.putEvent(currentTick, queuedEvent);
        }
        this.queuedEvents.clear();
    }

    /**
     * Metoda konczaca tick i rejestrujaca jego statystyki.
     */
    public synchronized void endTick()
    {
        final Player player = this.getOwner();

        final Tick currentTick = this.getCurrentTick();
        this.flushOldData(currentTick);

        final TimelineWalker walker = this.createWalkerForTicks(currentTick, 30);
        final boolean afterSpawn = walker.previous(PlayerSpawnTimelineEvent.class) != null /*|| player.getTicksLived() < 30*/;

        final boolean anyEventsInTick = this.events.hasEventsInTick(currentTick);
        final int ping = player.spigot().getPing();

        final PlayerTickInfoImpl playerTickInfo = new PlayerTickInfoImpl(player, currentTick, afterSpawn, anyEventsInTick, ping);
        this.tickInfo.put(currentTick, playerTickInfo);
    }

    /**
     * Usuwa stare zbedne dane z wewnetrznych map.
     *
     * @param currentTick Aktualnie trwajacy tick.
     */
    private void flushOldData(final Tick currentTick)
    {
        for (int i = 0; this.getTrackedTicks() >= this.ticksToTrack; i++)
        {
            final TickImpl tickToRemove = this.timelineManager.getPreviousTick(currentTick, this.ticksToTrack + i);

            this.events.clearEventsInTick(tickToRemove);
            this.tickInfo.remove(tickToRemove);
        }
    }

    private Tick getCurrentTick()
    {
        return this.timelineManager.getCurrentTick();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("ticksToTrack", this.ticksToTrack).append("queuedEvents", this.queuedEvents).append("events", this.events).append("tickInfo", this.tickInfo).toString();
    }
}
