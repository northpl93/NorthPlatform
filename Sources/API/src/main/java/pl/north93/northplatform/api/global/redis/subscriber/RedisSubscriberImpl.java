package pl.north93.northplatform.api.global.redis.subscriber;

import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.storage.StorageConnector;
import pl.north93.northplatform.api.global.storage.StringByteRedisCodec;

@Slf4j
@ToString(of = {"handler"})
public class RedisSubscriberImpl extends Component implements RedisSubscriber
{
    @Inject
    private StorageConnector storageConnector;
    private StatefulRedisPubSubConnection<String, byte[]> connection;
    private final MessageHandler handler = new MessageHandler();

    @Override
    protected void enableComponent()
    {
        this.connection = this.storageConnector.getRedisClient().connectPubSub(StringByteRedisCodec.INSTANCE);
        this.connection.addListener(this.handler);
    }

    @Override
    protected void disableComponent()
    {
        this.connection.sync().punsubscribe("*");

        this.handler.cleanup();
        this.connection.close();
    }

    @Override
    public void publish(final String channel, final byte[] message)
    {
        this.storageConnector.getRedis().publish(channel, message);
    }

    @Override
    public synchronized void subscribe(final String channel, final SubscriptionHandler handler, final boolean pattern)
    {
        this.handler.subscribe(channel, handler);
        if (pattern)
        {
            this.connection.sync().psubscribe(channel);
        }
        else
        {
            this.connection.sync().subscribe(channel);
        }
    }

    @Override
    public synchronized void unSubscribe(final String channel)
    {
        this.handler.unsubscribe(channel);
        this.connection.sync().unsubscribe(channel);
    }
}
