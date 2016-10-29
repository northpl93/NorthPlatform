package pl.north93.zgame.api.global.redis.rpc;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class RpcCustomTarget implements RpcTarget
{
    private final String targetId;

    public RpcCustomTarget(final String targetId)
    {
        this.targetId = "rpc:" + targetId;
    }

    @Override
    public String getRpcChannelName()
    {
        return this.targetId;
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

        final RpcCustomTarget that = (RpcCustomTarget) o;

        return this.targetId.equals(that.targetId);

    }

    @Override
    public int hashCode()
    {
        return this.targetId.hashCode();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("targetId", this.targetId).toString();
    }
}
