package pl.north93.zgame.api.global.redis.subscriber;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.ApiCore;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;
import redis.clients.util.SafeEncoder;

public class RedisSubscriberImpl implements RedisSubscriber
{
    private final ApiCore                          api                    = API.getApiCore();
    private final SubscriptionReceiver             subscriptionReceiver   = new SubscriptionReceiver();
    private final Map<String, SubscriptionHandler> subscriptionHandlerMap = new HashMap<>();
    private boolean subscriberScheduled;

    @Override
    public void subscribe(final String channel, final SubscriptionHandler handler)
    {
        this.subscriptionHandlerMap.put(channel, handler);
        API.debug("Added subscription for " + channel);
        if (! this.subscriberScheduled)
        {
            API.debug("Subscriber task has been scheduled");
            this.api.getPlatformConnector().runTaskAsynchronously(new SubscriberTask());
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
            RedisSubscriberImpl.this.api.getLogger().info("Subscriber task started");
            try (final Jedis jedis = RedisSubscriberImpl.this.api.getJedis().getResource())
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
                RedisSubscriberImpl.this.api.getLogger().warning("Received an message from unhandled channel " + channel);
                return;
            }

            try
            {
                subscriptionHandler.handle(channel, message);
            }
            catch (final Exception e)
            {
                RedisSubscriberImpl.this.api.getLogger().log(Level.SEVERE, "An exception has been thrown while handling message from Redis. Channel:" + channel + ", Message:" + Arrays.toString(message), e);
            }
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("api", this.api).append("subscriptionHandlerMap", this.subscriptionHandlerMap).toString();
    }
}
