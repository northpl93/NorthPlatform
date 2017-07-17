package pl.north93.zgame.api.global.redis.event.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.redis.event.INetEvent;

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
    public void invoke(final INetEvent event)
    {
        try
        {
            this.method.invoke(this.instance, event);
        }
        catch (final IllegalAccessException | InvocationTargetException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("instance", this.instance).append("method", this.method).toString();
    }
}
