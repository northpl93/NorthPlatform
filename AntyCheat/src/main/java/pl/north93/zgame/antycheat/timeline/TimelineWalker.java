package pl.north93.zgame.antycheat.timeline;

import java.util.Iterator;

public interface TimelineWalker extends Iterator<TimelineEvent>
{
    TimelineEvent first();

    TimelineEvent last();

    TimelineEvent next(Class<? extends TimelineEvent> type);

    boolean hasPrevious();

    TimelineEvent previous();

    TimelineEvent previous(Class<? extends TimelineEvent> type);
}
