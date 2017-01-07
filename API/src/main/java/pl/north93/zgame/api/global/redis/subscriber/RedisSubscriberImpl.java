package pl.north93.zgame.api.global.redis.subscriber;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.data.StorageConnector;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;
import redis.clients.util.SafeEncoder;

public class RedisSubscriberImpl extends Component implements RedisSubscriber
{
    private final SubscriptionReceiver             subscriptionReceiver   = new SubscriptionReceiver();
    private final Map<String, SubscriptionHandler> subscriptionHandlerMap = new HashMap<>();
    private final ExecutorService                  executor               = Executors.newCachedThreadPool();
    private final CountDownLatch                   subscriptionLock       = new CountDownLatch(1);
    private       boolean                          isSubscribed;
    @InjectComponent("API.Database.StorageConnector")
    private StorageConnector storageConnector;

    @Override
    protected void enableComponent()
    {
    }

    @Override
    protected void disableComponent()
    {
        this.unSubscribeAll();
        this.executor.shutdown();
    }

    @Override
    public void publish(final String channel, final byte[] message)
    {
        try (final Jedis jedis = this.storageConnector.getJedisPool().getResource())
        {
            jedis.publish(channel.getBytes(), message);
        }
    }

    @Override
    public void subscribe(final String channel, final SubscriptionHandler handler)
    {
        this.subscriptionHandlerMap.put(channel, handler);
        this.getApiCore().debug("Added subscription for " + channel);
        if (! this.subscriptionReceiver.isSubscribed() && ! this.isSubscribed)
        {
            this.isSubscribed = true;
            this.executor.execute(() ->
            {
                this.getLogger().info("jedis.subscribe invoked");
                try (final Jedis jedis = this.storageConnector.getJedisPool().getResource())
                {
                    jedis.subscribe(this.subscriptionReceiver, channel.getBytes());
                }
            });
            return;
        }
        try
        {
            this.subscriptionLock.await();
        }
        catch (final InterruptedException e)
        {
            e.printStackTrace();
        }
        this.subscriptionReceiver.subscribe(channel.getBytes());
    }

    @Override
    public void unSubscribe(final String channel)
    {
        if (this.subscriptionHandlerMap.remove(channel) != null)
        {
            this.subscriptionReceiver.unsubscribe(channel.getBytes());
            this.getApiCore().debug("Removed subscription for " + channel);
        }
    }

    @Override
    public void unSubscribeAll()
    {
        this.subscriptionHandlerMap.keySet().stream().map(String::getBytes).forEach(this.subscriptionReceiver::unsubscribe);
        this.subscriptionHandlerMap.clear();
    }

    private class SubscriptionReceiver extends BinaryJedisPubSub
    {
        @Override
        public void onSubscribe(final byte[] channel, final int subscribedChannels)
        {
            RedisSubscriberImpl.this.subscriptionLock.countDown();
        }

        @Override
        public void onMessage(final byte[] byteChannel, final byte[] message)
        {
            final String channel = SafeEncoder.encode(byteChannel);
            final SubscriptionHandler subscriptionHandler = RedisSubscriberImpl.this.subscriptionHandlerMap.get(channel);

            if (subscriptionHandler == null)
            {
                RedisSubscriberImpl.this.getApiCore().getLogger().warning("Received an message from unhandled channel " + channel);
                return;
            }

            RedisSubscriberImpl.this.executor.execute(() ->
            {
                try
                {
                    subscriptionHandler.handle(channel, message);
                }
                catch (final Exception e)
                {
                    RedisSubscriberImpl.this.getApiCore().getLogger().log(Level.SEVERE, "An exception has been thrown while handling message from Redis. Channel:" + channel + ", Message:" + Arrays.toString(message), e);
                }
            });
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("subscriptionHandlerMap", this.subscriptionHandlerMap).toString();
    }
}
