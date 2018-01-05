package pl.north93.zgame.antycheat.timeline.impl;

import java.util.List;
import java.util.ListIterator;

import pl.north93.zgame.antycheat.timeline.TimelineEvent;
import pl.north93.zgame.antycheat.timeline.TimelineWalker;

/*default*/ class TimelineWalkerImpl implements TimelineWalker
{
    private final int                         size;
    private final ListIterator<TimelineEvent> iterator;

    public TimelineWalkerImpl(final List<TimelineEvent> events)
    {
        this.size = events.size();
        // start od ostatniego wydarzenia jest chyba bardziej uzyteczny niz od pierwszego
        this.iterator = events.listIterator(Math.max(this.size - 1, 0));
    }

    @Override
    public TimelineEvent first()
    {
        if (this.size == 0)
        {
            return null;
        }

        while (this.iterator.previousIndex() >= 0)
        {
            this.iterator.previous();
        }
        return this.iterator.next();
    }

    @Override
    public TimelineEvent last()
    {
        if (this.size == 0)
        {
            return null;
        }

        while (this.iterator.nextIndex() < this.size)
        {
            this.iterator.next();
        }
        return this.iterator.previous();
    }

    @Override
    public TimelineEvent next(final Class<? extends TimelineEvent> type)
    {
        while (this.iterator.hasNext())
        {
            final TimelineEvent timelineEvent = this.iterator.next();
            if (timelineEvent.getClass().isAssignableFrom(type))
            {
                return timelineEvent;
            }
        }
        return null;
    }

    @Override
    public boolean hasPrevious()
    {
        return this.iterator.hasPrevious();
    }

    @Override
    public TimelineEvent previous()
    {
        return this.iterator.previous();
    }

    @Override
    public TimelineEvent previous(final Class<? extends TimelineEvent> type)
    {
        while (this.iterator.hasPrevious())
        {
            final TimelineEvent timelineEvent = this.iterator.next();
            if (timelineEvent.getClass().isAssignableFrom(type))
            {
                return timelineEvent;
            }
        }
        return null;
    }

    @Override
    public boolean hasNext()
    {
        return this.iterator.hasNext();
    }

    @Override
    public TimelineEvent next()
    {
        return this.iterator.next();
    }
}
