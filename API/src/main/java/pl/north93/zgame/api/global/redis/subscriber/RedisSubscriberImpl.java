package pl.north93.zgame.api.global.redis.subscriber;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lambdaworks.redis.pubsub.RedisPubSubAdapter;
import com.lambdaworks.redis.pubsub.StatefulRedisPubSubConnection;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.storage.StorageConnector;
import pl.north93.zgame.api.global.storage.StringByteRedisCodec;

public class RedisSubscriberImpl extends Component implements RedisSubscriber
{
    private final Map<String, SubscriptionHandler> handlerMap = new ConcurrentHashMap<>();
    private StatefulRedisPubSubConnection<String, byte[]> connection;
    private ExecutorService                               executorService = Executors.newCachedThreadPool();
    @Inject
    private Logger                                        logger;
    @Inject
    private StorageConnector                              storageConnector;

    @Override
    protected void enableComponent()
    {
        this.connection = this.storageConnector.getRedisClient().connectPubSub(StringByteRedisCodec.INSTANCE);
        this.connection.addListener(new MessageHandler());
    }

    @Override
    protected void disableComponent()
    {
        this.unSubscribeAll();
        this.executorService.shutdown();
        try
        {
            // oczekujemy na pomyślne zakończenie wszystkich tasków przed zakończeniem
            this.executorService.awaitTermination(10, TimeUnit.SECONDS);
        }
        catch (final InterruptedException e)
        {
            this.getLogger().log(Level.SEVERE, "Interrupted while waiting for executor termination", e);
        }
        this.connection.close();
    }

    @Override
    public void publish(final String channel, final byte[] message)
    {
        this.storageConnector.getRedis().publish(channel, message);
    }

    @Override
    public void subscribe(final String channel, final SubscriptionHandler handler, final boolean pattern)
    {
        synchronized (this)
        {
            this.handlerMap.put(channel, handler);
            if (pattern)
            {
                this.connection.sync().psubscribe(channel);
            }
            else
            {
                this.connection.sync().subscribe(channel);
            }
        }
    }

    @Override
    public void unSubscribe(final String channel)
    {
        synchronized (this)
        {
            this.handlerMap.remove(channel);
            this.connection.sync().unsubscribe(channel);
        }
    }

    @Override
    public void unSubscribeAll()
    {
        synchronized (this)
        {
            this.handlerMap.clear();
            this.connection.sync().punsubscribe("*");
        }
    }

    private class MessageHandler extends RedisPubSubAdapter<String, byte[]>
    {
        @Override
        public void message(final String channel, final byte[] message)
        {
            final SubscriptionHandler handler = RedisSubscriberImpl.this.handlerMap.get(channel);
            this.handle(handler, channel, message);
        }

        @Override
        public void message(final String pattern, final String channel, final byte[] message)
        {
            final SubscriptionHandler handler = RedisSubscriberImpl.this.handlerMap.get(pattern);
            this.handle(handler, channel, message);
        }

        private void handle(final SubscriptionHandler handler, final String channel, final byte[] message)
        {
            if (handler == null)
            {
                RedisSubscriberImpl.this.logger.warning("Received message from unhandled channel: " + channel);
                return;
            }

            RedisSubscriberImpl.this.executorService.submit(() ->
            {
                try
                {
                    handler.handle(channel, message);
                }
                catch (final Throwable e)
                {
                    // executor moze wygluszyc wyjatek, dlatego recznie zajmiemy sie jego wyprintowaniem
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("handlerMap", this.handlerMap).append("connection", this.connection).toString();
    }
}
