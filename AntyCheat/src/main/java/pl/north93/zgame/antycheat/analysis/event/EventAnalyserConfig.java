package pl.north93.zgame.antycheat.analysis.event;

import java.util.HashSet;
import java.util.Set;

import lombok.ToString;

@ToString
public final class EventAnalyserConfig
{
    private final Set<Class<?>> eventWhitelist = new HashSet<>();
    private int order = 0;

    public void whitelistEvent(final Class<?> event)
    {
        this.eventWhitelist.add(event);
    }

    public void order(final int order)
    {
        this.order = order;
    }

    public Set<Class<?>> getEventWhitelist()
    {
        return this.eventWhitelist;
    }

    public int getOrder()
    {
        return this.order;
    }
}
