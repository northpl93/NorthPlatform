package pl.north93.zgame.api.economy.impl.netcontroller.rest;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class OperationInfo
{
    private UUID playerId;
    private double before;
    private double after;

    public OperationInfo(final UUID playerId, final double before, final double after)
    {
        this.playerId = playerId;
        this.before = before;
        this.after = after;
    }

    public UUID getPlayerId()
    {
        return this.playerId;
    }

    public double getBefore()
    {
        return this.before;
    }

    public double getAfter()
    {
        return this.after;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("playerId", this.playerId).append("before", this.before).append("after", this.after).toString();
    }
}
