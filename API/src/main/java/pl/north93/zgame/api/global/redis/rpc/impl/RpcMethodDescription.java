package pl.north93.zgame.api.global.redis.rpc.impl;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.redis.rpc.annotation.DoNotWaitForResponse;
import pl.north93.zgame.api.global.redis.rpc.annotation.Timeout;

class RpcMethodDescription
{
    private final Integer      id;
    private final Method       method;
    private final MethodHandle methodHandle;
    private final int          timeout;
    private final boolean      needsWaitForResponse;

    public RpcMethodDescription(final Integer id, final Method method)
    {
        this.id = id;
        this.method = method;
        try
        {
            this.methodHandle = MethodHandles.lookup().unreflect(method);
        }
        catch (final IllegalAccessException e)
        {
            throw new RuntimeException("Failed to unreflect method " + method.getName(), e);
        }
        this.needsWaitForResponse = this.checkWait(method);
        this.timeout = this.getTimeout(method);
    }

    private boolean checkWait(final Method method)
    {
        final boolean isReturnVoid = method.getReturnType() == void.class;

        if (method.isAnnotationPresent(DoNotWaitForResponse.class))
        {
            if (! isReturnVoid)
            {
                throw new RuntimeException("Annotation DoNotWaitForResponse present on method with non-void return type.");
            }
            return false;
        }

        return true;
    }

    private int getTimeout(final Method method)
    {
        if (method.isAnnotationPresent(Timeout.class))
        {
            return method.getAnnotation(Timeout.class).value() * 1_000;
        }

        return 1_000; // Default value
    }

    public Integer getId()
    {
        return this.id;
    }

    public Method getMethod()
    {
        return this.method;
    }

    public MethodHandle getMethodHandle()
    {
        return this.methodHandle;
    }

    public int getTimeout()
    {
        return this.timeout;
    }

    public boolean isNeedsWaitForResponse()
    {
        return this.needsWaitForResponse;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("id", this.id).append("method", this.method).append("methodHandle", this.methodHandle).append("timeout", this.timeout).append("needsWaitForResponse", this.needsWaitForResponse).toString();
    }
}
