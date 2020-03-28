package pl.north93.northplatform.api.global.redis.subscriber;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.lambdaworks.redis.pubsub.RedisPubSubAdapter;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString(of = "handlerMap")
class MessageHandler extends RedisPubSubAdapter<String, byte[]>
{
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Map<String, SubscriptionHandler> handlerMap = new ConcurrentHashMap<>();

    public void subscribe(final String channel, final SubscriptionHandler handler)
    {
        this.handlerMap.put(channel, handler);
    }

    public void unsubscribe(final String channel)
    {
        this.handlerMap.remove(channel);
    }

    public void cleanup()
    {
        this.executorService.shutdown();
        try
        {
            // oczekujemy na pomyślne zakończenie wszystkich tasków przed zakończeniem
            this.executorService.awaitTermination(10, TimeUnit.SECONDS);
        }
        catch (final InterruptedException e)
        {
            log.error("Interrupted while waiting for executor termination", e);
        }
    }

    @Override
    public void message(final String channel, final byte[] message)
    {
        final SubscriptionHandler handler = this.handlerMap.get(channel);
        this.handle(handler, channel, message);
    }

    @Override
    public void message(final String pattern, final String channel, final byte[] message)
    {
        final SubscriptionHandler handler = this.handlerMap.get(pattern);
        this.handle(handler, channel, message);
    }

    private void handle(final SubscriptionHandler handler, final String channel, final byte[] message)
    {
        if (handler == null)
        {
            log.warn("Received message from unhandled channel: {}", channel);
            return;
        }

        this.executorService.submit(() ->
        {
            try
            {
                handler.handle(channel, message);
            }
            catch (final Throwable e)
            {
                // executor moze wygluszyc wyjatek, dlatego recznie zajmiemy sie jego wyprintowaniem
                log.error("Exception thrown in redis channel {} handler", channel, e);
            }
        });
    }
}
