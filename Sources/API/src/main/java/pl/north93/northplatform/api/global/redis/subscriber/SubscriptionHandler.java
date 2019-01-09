package pl.north93.northplatform.api.global.redis.subscriber;

@FunctionalInterface
public interface SubscriptionHandler
{
    void handle(String channel, byte[] message);
}
