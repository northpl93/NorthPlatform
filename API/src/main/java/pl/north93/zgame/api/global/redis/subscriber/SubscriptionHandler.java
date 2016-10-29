package pl.north93.zgame.api.global.redis.subscriber;

public interface SubscriptionHandler
{
    void handle(String channel, byte[] message);
}
