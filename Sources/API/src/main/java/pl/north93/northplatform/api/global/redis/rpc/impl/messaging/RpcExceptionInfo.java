package pl.north93.northplatform.api.global.redis.rpc.impl.messaging;

import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class RpcExceptionInfo
{
    private String exceptionClass;
    private String message;

    public RpcExceptionInfo() // for serialization
    {
    }

    public RpcExceptionInfo(final Throwable throwable)
    {
        this.exceptionClass = throwable.getClass().getName();
        this.message = throwable.getMessage();
    }

    public String getExceptionClass()
    {
        return this.exceptionClass;
    }

    public void setExceptionClass(final String exceptionClass)
    {
        this.exceptionClass = exceptionClass;
    }

    @Nullable
    public String getMessage()
    {
        return this.message;
    }

    public void setMessage(final String message)
    {
        this.message = message;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("exceptionClass", this.exceptionClass).append("message", this.message).toString();
    }
}
