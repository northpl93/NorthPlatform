package pl.north93.northplatform.api.global.redis.event;

public interface IEventManager
{
    void callEvent(INetEvent event);
}
