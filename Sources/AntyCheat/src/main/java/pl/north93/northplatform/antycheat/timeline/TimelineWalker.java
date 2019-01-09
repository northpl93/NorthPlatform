package pl.north93.northplatform.antycheat.timeline;

import java.util.Iterator;

public interface TimelineWalker extends Iterator<TimelineEvent>
{
    TimelineEvent first();

    default <T extends TimelineEvent> T first(final Class<T> type)
    {
        this.first();
        return this.next(type);
    }

    TimelineEvent last();

    default <T extends TimelineEvent> T last(final Class<T> type)
    {
        this.last();
        return this.previous(type);
    }

    <T extends TimelineEvent> T next(Class<T> type);

    boolean hasPrevious();

    TimelineEvent previous();

    <T extends TimelineEvent> T previous(Class<T> type);

    boolean find(TimelineEvent event);
}
