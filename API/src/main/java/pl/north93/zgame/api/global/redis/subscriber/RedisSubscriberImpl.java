package pl.north93.zgame.api.global.redis.subscriber;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.Platform;
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
    @InjectComponent("API.Database.StorageConnector")
    private StorageConnector storageConnector;
    private boolean   subscriberScheduled;

    @Override
    protected void enableComponent()
    {
    }

    @Override
    protected void disableComponent()
    {
        this.unSubscribeAll();
    }

    @Override
    public void subscribe(final String channel, final SubscriptionHandler handler)
    {
        this.subscriptionHandlerMap.put(channel, handler);
        this.getApiCore().debug("Added subscription for " + channel);
        if (! this.subscriberScheduled)
        {
            this.getApiCore().debug("Subscriber task has been scheduled");
            if (this.getApiCore().getPlatform() == Platform.BUKKIT)
            {
                final Thread thread = new Thread(new SubscriberTask(), "Redis Subsciber");
                thread.setDaemon(true);
                thread.start();
            }
            else
            {
                this.getApiCore().getPlatformConnector().runTaskAsynchronously(new SubscriberTask());
            }
            this.subscriberScheduled = true;
            return;
        }
        if (! this.subscriptionReceiver.isSubscribed())
        {
            return;
        }
        this.subscriptionReceiver.subscribe(channel.getBytes());
    }

    @Override
    public void unSubscribe(final String channel)
    {
        if (this.subscriptionHandlerMap.remove(channel) != null)
        {
            this.subscriptionReceiver.unsubscribe(channel.getBytes());
        }
    }

    @Override
    public void unSubscribeAll()
    {
        this.subscriptionHandlerMap.keySet().stream().map(String::getBytes).forEach(this.subscriptionReceiver::unsubscribe);
        this.subscriptionHandlerMap.clear();
    }

    private class SubscriberTask implements Runnable
    {
        @Override
        public void run()
        {
            RedisSubscriberImpl.this.getApiCore().getLogger().info("Subscriber task started");
            try (final Jedis jedis = RedisSubscriberImpl.this.storageConnector.getJedisPool().getResource())
            {
                final Set<String> stringChannels = RedisSubscriberImpl.this.subscriptionHandlerMap.keySet();
                final Iterator<String> iterator = stringChannels.iterator();
                final byte[][] channels = new byte[stringChannels.size()][];
                int id = 0;
                while (iterator.hasNext())
                {
                    channels[id++] = iterator.next().getBytes();
                }

                jedis.subscribe(RedisSubscriberImpl.this.subscriptionReceiver, channels);
            }
        }
    }

    private class SubscriptionReceiver extends BinaryJedisPubSub
    {
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

            try
            {
                subscriptionHandler.handle(channel, message);
            }
            catch (final Exception e)
            {
                RedisSubscriberImpl.this.getApiCore().getLogger().log(Level.SEVERE, "An exception has been thrown while handling message from Redis. Channel:" + channel + ", Message:" + Arrays.toString(message), e);
            }
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("subscriptionHandlerMap", this.subscriptionHandlerMap).toString();
    }
}
