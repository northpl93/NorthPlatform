package pl.north93.zgame.antycheat.timeline.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import org.apache.commons.lang3.tuple.Pair;

import pl.north93.zgame.antycheat.timeline.Tick;
import pl.north93.zgame.antycheat.timeline.TimelineEvent;
import pl.north93.zgame.antycheat.timeline.TimelineWalker;

class LinkedEventsQueue
{
    private final TimelineManager timelineManager;
    private final Map<Tick, EventEntry> firstEventInTick = new HashMap<>(32);
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
        final EventEntry cursor = cursorAtEnd ? this.last : this.first;
        return new TimelineWalkerImpl(this.first, this.last, cursor);
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

    private synchronized TimelineWalkerImpl createWalkerInTickRange(final Tick firstTick, final Tick lastTick, final boolean cursorAtEnd)
    {
        final Pair<EventEntry, EventEntry> range = this.getFirstAndLastEventInTickRange(firstTick, lastTick);
        if (range == null)
        {
            return new TimelineWalkerImpl(null, null, null);
        }

        final EventEntry cursor = cursorAtEnd ? range.getRight() : range.getLeft();
        return new TimelineWalkerImpl(range.getLeft(), range.getRight(), cursor);
    }

    private synchronized @Nullable Pair<EventEntry, EventEntry> getFirstAndLastEventInTickRange(final Tick firstTick, final Tick lastTick)
    {
        Tick checkedFirstTick = firstTick;
        EventEntry firstEventEntry;
        while (true)
        {
            firstEventEntry = this.firstEventInTick.get(checkedFirstTick);
            if (firstEventEntry != null)
            {
                break;
            }

            checkedFirstTick = this.timelineManager.getNextTick(checkedFirstTick);
            if (checkedFirstTick == null || checkedFirstTick.getTickId() > lastTick.getTickId())
            {
                return null;
            }
        }

        EventEntry lastEventEntry = firstEventEntry;
        while (lastEventEntry.next != null && lastEventEntry.next.tick.getTickId() <= lastTick.getTickId())
        {
            lastEventEntry = lastEventEntry.next;
        }

        return Pair.of(firstEventEntry, lastEventEntry);
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

        public TimelineWalkerImpl(final LinkedEventsQueue.EventEntry lowerBound, final LinkedEventsQueue.EventEntry upperBound, final LinkedEventsQueue.EventEntry cursor)
        {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.cursor = cursor;
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
                return null;
            }

            return (this.cursor = this.lowerBound).event;
        }

        @Override
        public TimelineEvent last()
        {
            if (this.isEmpty())
            {
                return null;
            }

            return (this.cursor = this.upperBound).event;
        }

        @Override
        public TimelineEvent next(final Class<? extends TimelineEvent> type)
        {
            while (this.hasNext())
            {
                final TimelineEvent timelineEvent = this.next();
                if (type.isAssignableFrom(timelineEvent.getClass()))
                {
                    return timelineEvent;
                }
            }
            return null;
        }

        @Override
        public boolean hasPrevious()
        {
            return this.cursor != this.lowerBound;
        }

        @Override
        public TimelineEvent previous()
        {
            return (this.cursor = this.cursor.previous).event;
        }

        @Override
        public TimelineEvent previous(final Class<? extends TimelineEvent> type)
        {
            while (this.hasPrevious())
            {
                final TimelineEvent timelineEvent = this.previous();
                if (type.isAssignableFrom(timelineEvent.getClass()))
                {
                    return timelineEvent;
                }
            }
            return null;
        }

        @Override
        public boolean hasNext()
        {
            return this.cursor != this.upperBound;
        }

        @Override
        public TimelineEvent next()
        {
            return (this.cursor = this.cursor.next).event;
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
}