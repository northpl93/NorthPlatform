package pl.north93.zgame.antycheat.analysis.event;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class EventAnalyserConfig
{
    private final Set<Class<?>> eventWhitelist = new HashSet<>();

    public void whitelistEvent(final Class<?> event)
    {
        this.eventWhitelist.add(event);
    }

    public Set<Class<?>> getEventWhitelist()
    {
        return this.eventWhitelist;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("eventWhitelist", this.eventWhitelist).toString();
    }
}
