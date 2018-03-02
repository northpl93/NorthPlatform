package pl.north93.zgame.antycheat.timeline.impl;

import static pl.north93.zgame.antycheat.timeline.impl.LinkedEventsQueue.IteratorStatus.AFTER;
import static pl.north93.zgame.antycheat.timeline.impl.LinkedEventsQueue.IteratorStatus.BEFORE;
import static pl.north93.zgame.antycheat.timeline.impl.LinkedEventsQueue.IteratorStatus.IN;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import com.google.common.collect.ImmutableList;

import org.apache.commons.lang3.tuple.Pair;

import pl.north93.zgame.antycheat.timeline.Tick;
import pl.north93.zgame.antycheat.timeline.TimelineEvent;
import pl.north93.zgame.antycheat.timeline.TimelineWalker;

class LinkedEventsQueue
{
    private final TimelineManager timelineManager;
    private final Map<Tick, EventEntry> firstEventInTick = new HashMap<>(64);
    private final Map<Tick, EventEntry> lastEventInTick = new HashMap<>(64);
    private EventEntry first;
    private EventEntry last;

    public LinkedEventsQueue(final TimelineManager timelineManager)
    {
        this.timelineManager = timelineManager;
    }

    /**
     * Dodaje podany event na koncu listy i laczy go z podanym tickiem.
     *
     * @param tick Tick z ktorym jest powiazany dany event.
     * @param event Event ktory dodajemy.
     */
    /*default*/ synchronized void putEvent(final Tick tick, final TimelineEvent event)
    {
        if (this.first == null)
        {
            final EventEntry eventEntry = new EventEntry(null, tick, event);
            this.firstEventInTick.put(tick, eventEntry);
            this.lastEventInTick.put(tick, eventEntry);
            this.first = eventEntry;
            this.last = eventEntry;
        }
        else
        {
            final int lastTickId = this.last.tick.getTickId();
            if (lastTickId > tick.getTickId())
            {
                throw new IllegalArgumentException("Can't add events to past ticks");
            }

            final EventEntry eventEntry = new EventEntry(this.last, tick, event);
            if (lastTickId != tick.getTickId())
            {
                this.firstEventInTick.put(tick, eventEntry);
            }
            this.lastEventInTick.put(tick, eventEntry);
            this.last.next = eventEntry;
            this.last = eventEntry;
        }
    }

    /**
     * Usuwa wszystkie eventy znajdujace sie w danym ticku.
     * Najczesciej uzywane do czyszczenia najstarszego ticku.
     *
     * @param tick Tick z ktorego skaskowac wszystkie eventy.
     */
    /*default*/ synchronized void clearEventsInTick(final Tick tick)
    {
        final Pair<EventEntry, EventEntry> firstAndLast = this.getFirstAndLastEventInTick(tick);
        if (firstAndLast == null)
        {
            return;
        }

        this.firstEventInTick.remove(tick);
        this.lastEventInTick.remove(tick);

        final EventEntry first = firstAndLast.getLeft();
        final EventEntry last = firstAndLast.getRight();

        if (first.previous == null)
        {
            this.first = last.next;
        }
        else
        {
            first.previous.next = last.next;
        }

        if (last.next == null)
        {
            this.last = first.previous;
        }
        else
        {
            last.next.previous = first.previous;
        }
    }

    /*default*/ synchronized TimelineWalkerImpl createWalkerInTickRangeWithCursorAtEnd(final Tick firstTick, final Tick lastTick)
    {
        return this.createWalkerInTickRange(firstTick, lastTick, true);
    }

    /*default*/ synchronized TimelineWalkerImpl createWalkerInFullRange(final boolean cursorAtEnd)
    {
        return new TimelineWalkerImpl(this.first, this.last, cursorAtEnd);
    }

    /**
     * Sprawdza czy w danym ticku mamy zarejestrowane jakies eventy.
     *
     * @param tick Tick do sprawdzenia.
     * @return True jesli zarejestrowalismy jakies eventy dla tego ticka.
     */
    /*default*/ synchronized boolean hasEventsInTick(final Tick tick)
    {
        return this.firstEventInTick.containsKey(tick);
    }

    /*default*/ synchronized Collection<TimelineEvent> getEventsInTick(final Tick tick)
    {
        final TimelineWalkerImpl walker = this.createWalkerInTickRange(tick, tick, false);
        return ImmutableList.copyOf(walker);
    }

    /*default*/ synchronized Collection<TimelineEvent> getEvents()
    {
        final TimelineWalkerImpl fullRange = this.createWalkerInFullRange(false);
        return ImmutableList.copyOf(fullRange);
    }

    /*default*/ synchronized void clear()
    {
        this.first = null;
        this.last = null;
        this.firstEventInTick.clear();
        this.lastEventInTick.clear();
    }

    private synchronized TimelineWalkerImpl createWalkerInTickRange(final Tick firstTick, final Tick lastTick, final boolean cursorAtEnd)
    {
        final Pair<EventEntry, EventEntry> range = this.getFirstAndLastEventInTickRange(firstTick, lastTick);
        if (range == null)
        {
            return new TimelineWalkerImpl(null, null, false);
        }

        return new TimelineWalkerImpl(range.getLeft(), range.getRight(), cursorAtEnd);
    }

    private synchronized @Nullable Pair<EventEntry, EventEntry> getFirstAndLastEventInTickRange(final Tick firstTick, final Tick lastTick)
    {
        if (firstTick.getTickId() > lastTick.getTickId())
        {
            return null; // brak danych
        }

        final EventEntry firstEvent = this.firstEventInTick.get(firstTick);
        if (firstEvent == null)
        {
            final TickImpl nextTick = this.timelineManager.getNextTick(firstTick);
            if (nextTick == null)
            {
                // próbujemy wybiegac w przyszlosc, nie wykonujemy kolejnego rekurencyjnego calla
                return null;
            }
            return this.getFirstAndLastEventInTickRange(nextTick, lastTick);
        }

        final EventEntry lastEvent = this.lastEventInTick.get(lastTick);
        if (lastEvent == null)
        {
            // tutaj nigdy nic sie nie zepsuje bo mamy checka na poczatku metody
            return this.getFirstAndLastEventInTickRange(firstTick, this.timelineManager.getPreviousTick(lastTick, 1));
        }

        return Pair.of(firstEvent, lastEvent);
    }

    private synchronized @Nullable Pair<EventEntry, EventEntry> getFirstAndLastEventInTick(final Tick tick)
    {
        return this.getFirstAndLastEventInTickRange(tick, tick);
    }

    class TimelineWalkerImpl implements TimelineWalker
    {
        private final LinkedEventsQueue.EventEntry lowerBound;
        private final LinkedEventsQueue.EventEntry upperBound;
        private LinkedEventsQueue.EventEntry cursor;
        private IteratorStatus status;

        public TimelineWalkerImpl(final LinkedEventsQueue.EventEntry lowerBound, final LinkedEventsQueue.EventEntry upperBound, final boolean atEnd)
        {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            if (atEnd)
            {
                this.status = AFTER;
            }
            else
            {
                this.status = IteratorStatus.BEFORE;
            }
        }

        private boolean isEmpty()
        {
            return this.lowerBound == null || this.upperBound == null;
        }

        @Override
        public TimelineEvent first()
        {
            if (this.isEmpty())
            {
                throw new NoSuchElementException();
            }

            return (this.cursor = this.lowerBound).event;
        }

        @Override
        public TimelineEvent last()
        {
            if (this.isEmpty())
            {
                throw new NoSuchElementException();
            }

            return (this.cursor = this.upperBound).event;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends TimelineEvent> T next(final Class<T> type)
        {
            while (this.hasNext())
            {
                final TimelineEvent timelineEvent = this.next();
                if (type.isAssignableFrom(timelineEvent.getClass()))
                {
                    return (T) timelineEvent;
                }
            }
            return null;
        }

        @Override
        public boolean hasPrevious()
        {
            return ! this.isEmpty() && this.status != BEFORE;
        }

        @Override
        public TimelineEvent previous()
        {
            if (! this.hasPrevious())
            {
                throw new NoSuchElementException();
            }
            else if (this.status == AFTER)
            {
                this.cursor = this.upperBound;
                this.status = IN;
            }

            final TimelineEvent event = this.cursor.event;
            this.cursor = this.cursor.previous;
            if (this.cursor == null || this.cursor.next == this.lowerBound)
            {
                this.status = BEFORE;
            }
            return event;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends TimelineEvent> T previous(final Class<T> type)
        {
            while (this.hasPrevious())
            {
                final TimelineEvent timelineEvent = this.previous();
                if (type.isAssignableFrom(timelineEvent.getClass()))
                {
                    return (T) timelineEvent;
                }
            }
            return null;
        }

        @Override
        public boolean find(final TimelineEvent event)
        {
            for (TimelineEvent check = this.last(); this.hasPrevious(); check = this.previous())
            {
                if (check == event)
                {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean hasNext()
        {
            return ! this.isEmpty() && this.status != AFTER;
        }

        @Override
        public TimelineEvent next()
        {
            if (! this.hasNext())
            {
                throw new NoSuchElementException();
            }
            else if (this.status == BEFORE)
            {
                this.cursor = this.lowerBound;
                this.status = IN;
            }

            final TimelineEvent event = this.cursor.event;
            this.cursor = this.cursor.next;
            if (this.cursor == null || this.cursor.previous == this.upperBound)
            {
                this.status = AFTER;
            }
            return event;
        }
    }

    class EventEntry
    {
        private @Nullable EventEntry previous;
        private @Nullable EventEntry next;
        private final Tick          tick;
        private final TimelineEvent event;

        public EventEntry(final @Nullable EventEntry previous, final @Nonnull Tick tick, final @Nonnull TimelineEvent event)
        {
            this.previous = previous;
            this.tick = tick;
            this.event = event;
        }
    }

    enum IteratorStatus
    {
        BEFORE, IN, AFTER
    }
}