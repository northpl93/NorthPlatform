package pl.north93.zgame.api.global.redis.subscriber;

public interface RedisSubscriber
{
    void subscribe(String channel, SubscriptionHandler handler);

    default void subscribe(String channel)
    {
        this.subscribe(channel, null);
    }

    void unSubscribeAll();
}
