package pl.north93.zgame.api.global.redis.event.impl;

import java.lang.reflect.Method;

import lombok.ToString;
import pl.north93.zgame.api.global.redis.event.INetEvent;

@ToString
public class MethodInvocationHandler implements IEventInvocationHandler
{
    private final Object instance;
    private final Method method;

    public MethodInvocationHandler(final Object instance, final Method method)
    {
        this.instance = instance;
        this.method = method;
        this.method.setAccessible(true);
    }

    @Override
    public void invoke(final INetEvent event) throws Exception
    {
        this.method.invoke(this.instance, event);
    }
}
