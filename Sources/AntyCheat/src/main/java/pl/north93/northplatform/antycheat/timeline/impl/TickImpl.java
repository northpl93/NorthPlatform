package pl.north93.northplatform.antycheat.timeline.impl;

import com.google.common.base.Preconditions;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.antycheat.timeline.Tick;

/*default*/ class TickImpl implements Tick
{
    private final int tickId;
    private boolean completed;

    public TickImpl(final int tickId)
    {
        this.tickId = tickId;
    }

    public TickImpl(final int tickId, final boolean completed)
    {
        this.tickId = tickId;
        this.completed = completed;
    }

    @Override
    public int getTickId()
    {
        return this.tickId;
    }

    @Override
    public boolean isCompleted()
    {
        return this.completed;
    }

    public void makeCompleted()
    {
        Preconditions.checkState(! this.completed, "Tick is already completed!");
        this.completed = true;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || this.getClass() != o.getClass())
        {
            return false;
        }
        final TickImpl tick = (TickImpl) o;
        return this.tickId == tick.tickId;
    }

    @Override
    public int hashCode()
    {
        return Integer.hashCode(this.tickId);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("tickId", this.tickId).append("completed", this.completed).toString();
    }
}
