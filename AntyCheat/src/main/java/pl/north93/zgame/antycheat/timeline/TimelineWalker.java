package pl.north93.zgame.antycheat.timeline;

import java.util.Iterator;

public interface TimelineWalker extends Iterator<TimelineEvent>
{
    TimelineEvent first();

    TimelineEvent last();

    <T extends TimelineEvent> T next(Class<T> type);

    boolean hasPrevious();

    TimelineEvent previous();

    <T extends TimelineEvent> T previous(Class<T> type);
}
