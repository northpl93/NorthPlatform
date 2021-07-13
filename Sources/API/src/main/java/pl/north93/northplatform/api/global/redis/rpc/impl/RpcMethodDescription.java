package pl.north93.northplatform.api.global.redis.rpc.impl;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

import lombok.ToString;
import pl.north93.northplatform.api.global.redis.rpc.annotation.DoNotWaitForResponse;
import pl.north93.northplatform.api.global.redis.rpc.annotation.Timeout;

@ToString
class RpcMethodDescription
{
    private final int id;
    private final int timeout;
    private final String name;
    private final MethodHandle methodHandle;
    private final boolean returnsVoid;
    private final boolean needsWaitForResponse;

    public RpcMethodDescription(final int id, final Method method)
    {
        this.id = id;
        this.name = method.getName();
        try
        {
            this.methodHandle = MethodHandles.lookup().unreflect(method);
        }
        catch (final IllegalAccessException e)
        {
            throw new RuntimeException("Failed to unreflect method " + method.getName(), e);
        }
        this.returnsVoid = method.getReturnType() == void.class;
        this.needsWaitForResponse = this.checkWait(method);
        this.timeout = this.getTimeout(method);
    }

    private boolean checkWait(final Method method)
    {
        if (method.isAnnotationPresent(DoNotWaitForResponse.class))
        {
            if (! this.returnsVoid)
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

    public Object invokeWithArguments(final Object[] args) throws Throwable
    {
        return this.methodHandle.invokeWithArguments(args);
    }

    public int getId()
    {
        return this.id;
    }

    public String getName()
    {
        return this.name;
    }

    public boolean isReturnsVoid()
    {
        return this.returnsVoid;
    }

    public int getTimeout()
    {
        return this.timeout;
    }

    public boolean isNeedsWaitForResponse()
    {
        return this.needsWaitForResponse;
    }
}
