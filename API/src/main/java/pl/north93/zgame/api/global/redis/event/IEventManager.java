package pl.north93.zgame.api.global.redis.event;

public interface IEventManager
{
    void callEvent(INetEvent event);
}
