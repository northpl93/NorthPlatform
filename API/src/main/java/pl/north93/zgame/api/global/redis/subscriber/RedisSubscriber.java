package pl.north93.zgame.api.global.redis.subscriber;

public interface RedisSubscriber
{
    void publish(String channel, byte[] message);

    void subscribe(String channel, SubscriptionHandler handler);

    default void subscribe(String channel)
    {
        this.subscribe(channel, null);
    }

    void unSubscribe(String channel);

    void unSubscribeAll();
}
