package pl.north93.zgame.antycheat.timeline.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import pl.north93.zgame.antycheat.event.AbstractTimelineEvent;
import pl.north93.zgame.antycheat.timeline.Tick;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TickManager.class})
public class EventsQueueTest
{
    private static int currentTick;
    @Mock
    private TickManager       tickManager;
    private LinkedEventsQueue queue;

    @Before
    public void setup() throws Exception
    {
        final TimelineManager timelineManager = Mockito.mock(TimelineManager.class);

        PowerMockito.when(this.tickManager, MemberMatcher.method(TickManager.class, "getCurrentIdFromServer")).withNoArguments().then(data -> currentTick);
        PowerMockito.when(this.tickManager.getCurrentTick()).thenCallRealMethod();
        Whitebox.setInternalState(timelineManager, "tickManager", this.tickManager);

        when(timelineManager.getCurrentTick()).thenCallRealMethod();
        when(timelineManager.getNextTick(any())).thenCallRealMethod();
        //when(timelineManager.getPreviousTick(any(), any())).thenCallRealMethod();

        this.queue = new LinkedEventsQueue(timelineManager);
    }

    @Test
    public void emptyQueue()
    {
        assertEquals(0, this.queue.getEvents().size());
    }

    @Test
    public void queueWithOneEvent()
    {
        final Tick tick1 = this.nextTick();
        this.queue.putEvent(tick1, new DummyEvent());

        assertEquals(1, this.queue.getEvents().size());
        assertEquals(1, this.queue.getEventsInTick(tick1).size());
    }

    @Test
    public void queueWithThreeEventsTwoTicks()
    {
        final Tick tick1 = this.nextTick();
        this.queue.putEvent(tick1, new DummyEvent());

        final Tick tick2 = this.nextTick();
        this.queue.putEvent(tick2, new DummyEvent());
        this.queue.putEvent(tick2, new DummyEvent());

        assertEquals(3, this.queue.getEvents().size());
        assertEquals(1, this.queue.getEventsInTick(tick1).size());
        assertEquals(2, this.queue.getEventsInTick(tick2).size());
    }

    @Test
    public void queueWithManyEventsThreeTicks()
    {
        final Tick tick1 = this.nextTick();
        for (int i = 0; i < 5; i++)
        {
            this.queue.putEvent(tick1, new DummyEvent());
        }

        final Tick tick2 = this.nextTick();
        for (int i = 0; i < 10; i++)
        {
            this.queue.putEvent(tick2, new DummyEvent());
        }

        final Tick tick3 = this.nextTick();
        for (int i = 0; i < 100; i++)
        {
            this.queue.putEvent(tick3, new DummyEvent());
        }

        assertEquals(115, this.queue.getEvents().size());
        assertEquals(5, this.queue.getEventsInTick(tick1).size());
        assertEquals(10, this.queue.getEventsInTick(tick2).size());
        assertEquals(100, this.queue.getEventsInTick(tick3).size());
    }

    @Test
    public void specialWalkerFunctionsWithCursorAtBegin()
    {
        final Tick tick1 = this.nextTick();
        this.queue.putEvent(tick1, new DummyEvent());

        final LinkedEventsQueue.TimelineWalkerImpl walker = this.queue.createWalkerInFullRange(false);

        assertEquals(false, walker.hasPrevious());
        assertEquals(true, walker.hasNext());

        walker.next();

        assertEquals(true, walker.hasPrevious());
        assertEquals(false, walker.hasNext());

        walker.previous();

        assertEquals(false, walker.hasPrevious());
        assertEquals(true, walker.hasNext());
    }

    @Test
    public void specialWalkerFunctionsWithCursorAtEnd()
    {
        final Tick tick1 = this.nextTick();
        this.queue.putEvent(tick1, new DummyEvent());

        final LinkedEventsQueue.TimelineWalkerImpl walker = this.queue.createWalkerInFullRange(true);

        assertEquals(true, walker.hasPrevious());
        assertEquals(false, walker.hasNext());

        walker.previous();

        assertEquals(false, walker.hasPrevious());
        assertEquals(true, walker.hasNext());

        walker.next();

        assertEquals(true, walker.hasPrevious());
        assertEquals(false, walker.hasNext());
    }

    private Tick nextTick()
    {
        currentTick++;
        return this.tickManager.getCurrentTick();
    }

    public static final class DummyEvent extends AbstractTimelineEvent
    {
        public DummyEvent()
        {
            super(null);
        }
    }
}
