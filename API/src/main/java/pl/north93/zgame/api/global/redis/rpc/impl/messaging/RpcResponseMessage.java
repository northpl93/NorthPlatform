package pl.north93.zgame.api.global.redis.rpc.impl.messaging;

import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class RpcResponseMessage
{
    private Integer requestId;
    @Nullable
    private Object  response;

    public RpcResponseMessage()
    {
    }

    public RpcResponseMessage(final Integer requestId, final Object response)
    {
        this.requestId = requestId;
        this.response = response;
    }

    public Integer getRequestId()
    {
        return this.requestId;
    }

    public void setRequestId(final Integer requestId)
    {
        this.requestId = requestId;
    }

    public Boolean isException()
    {
        return this.response instanceof RpcExceptionInfo;
    }

    public Object getResponse()
    {
        return this.response;
    }

    public void setResponse(final Object response)
    {
        this.response = response;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("requestId", this.requestId).append("response", this.response).toString();
    }
}
