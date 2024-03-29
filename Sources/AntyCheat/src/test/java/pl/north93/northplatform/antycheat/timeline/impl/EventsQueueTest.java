package pl.north93.northplatform.antycheat.timeline.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import pl.north93.northplatform.antycheat.event.AbstractTimelineEvent;
import pl.north93.northplatform.antycheat.timeline.Tick;

@Disabled // todo implement tests without PowerMock & ensure it works with JUnit 5
public class EventsQueueTest
{
    private static int currentTick;
    @Mock
    private TickManager       tickManager;
    private LinkedEventsQueue queue;

    @BeforeEach
    public void setup() throws Exception
    {
        final TimelineManager timelineManager = Mockito.mock(TimelineManager.class);

//        PowerMockito.when(this.tickManager, MemberMatcher.method(TickManager.class, "getCurrentIdFromServer")).withNoArguments().then(data -> currentTick);
//        PowerMockito.when(this.tickManager.getCurrentTick()).thenCallRealMethod();
//        Whitebox.setInternalState(timelineManager, "tickManager", this.tickManager);

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

        assertFalse(walker.hasPrevious());
        assertTrue(walker.hasNext());

        walker.next();

        assertTrue(walker.hasPrevious());
        assertFalse(walker.hasNext());

        walker.previous();

        assertFalse(walker.hasPrevious());
        assertTrue(walker.hasNext());
    }

    @Test
    public void specialWalkerFunctionsWithCursorAtEnd()
    {
        final Tick tick1 = this.nextTick();
        this.queue.putEvent(tick1, new DummyEvent());

        final LinkedEventsQueue.TimelineWalkerImpl walker = this.queue.createWalkerInFullRange(true);

        assertTrue(walker.hasPrevious());
        assertFalse(walker.hasNext());

        walker.previous();

        assertFalse(walker.hasPrevious());
        assertTrue(walker.hasNext());

        walker.next();

        assertTrue(walker.hasPrevious());
        assertFalse(walker.hasNext());
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
