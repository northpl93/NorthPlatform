package pl.north93.northplatform.api.global.redis.subscriber;

public interface RedisSubscriber
{
    void publish(String channel, byte[] message);

    void subscribe(String channel, SubscriptionHandler handler, boolean pattern);

    default void subscribe(String channel, SubscriptionHandler handler)
    {
        this.subscribe(channel, handler, false);
    }

    default void subscribe(String channel)
    {
        this.subscribe(channel, null);
    }

    void unSubscribe(String channel);
}
