package pl.north93.zgame.api.global.redis.subscriber;

@FunctionalInterface
public interface SubscriptionHandler
{
    void handle(String channel, byte[] message);
}
