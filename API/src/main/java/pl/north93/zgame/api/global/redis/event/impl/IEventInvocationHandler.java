package pl.north93.zgame.api.global.redis.event.impl;

import pl.north93.zgame.api.global.redis.event.INetEvent;

public interface IEventInvocationHandler
{
    void invoke(INetEvent event);
}
