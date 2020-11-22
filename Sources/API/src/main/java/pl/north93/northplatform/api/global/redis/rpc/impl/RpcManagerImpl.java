package pl.north93.northplatform.api.global.redis.rpc.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.redis.rpc.IRpcManager;
import pl.north93.northplatform.api.global.redis.rpc.IRpcTarget;
import pl.north93.northplatform.api.global.redis.rpc.exceptions.RpcUnimplementedException;
import pl.north93.northplatform.api.global.redis.rpc.impl.messaging.RpcExceptionInfo;
import pl.north93.northplatform.api.global.redis.rpc.impl.messaging.RpcInvokeMessage;
import pl.north93.northplatform.api.global.redis.rpc.impl.messaging.RpcResponseMessage;
import pl.north93.northplatform.api.global.redis.subscriber.RedisSubscriber;
import pl.north93.northplatform.api.global.storage.StorageConnector;
import pl.north93.serializer.platform.NorthSerializer;

@Slf4j
public class RpcManagerImpl extends Component implements IRpcManager
{
    @Inject
    private StorageConnector storageConnector;
    @Inject
    private RedisSubscriber redisSubscriber;
    @Inject
    private NorthSerializer<byte[], byte[]> msgPack;
    private final RpcProxyCache rpcProxyCache = new RpcProxyCache(this);
    private final IntObjectMap<RpcResponseHandler> responseHandlerMap = new IntObjectHashMap<>();
    private final IntObjectMap<RpcResponseLock> locks = new IntObjectHashMap<>();
    private final Map<Class<?>, RpcObjectDescription> descriptionCache = new ConcurrentHashMap<>();

    @Override
    protected void enableComponent()
    {
        this.addListeningContext0(this.getApiCore().getId());
    }

    @Override
    protected void disableComponent()
    {
    }

    @Override
    public void addListeningContext(final String id)
    {
        if (this.getStatus().isDisabled())
        {
            log.warn("Tried to register listeningContext while RpcManager is disabled");
            return;
        }
        this.addListeningContext0(id);
    }

    private synchronized void addListeningContext0(final String id)
    {
        log.debug("addListeningContext0({})", id);
        this.redisSubscriber.subscribe("rpc:" + id + ":invoke", this::handleMethodInvocation);
        this.redisSubscriber.subscribe("rpc:" + id + ":response", this::handleResponse);
    }

    @Override
    public synchronized void addRpcImplementation(final Class<?> classInterface, final Object implementation)
    {
        log.debug("addRpcImplementation({}, {})", classInterface, implementation.getClass().getName());
        this.responseHandlerMap.put(classInterface.getName().hashCode(), new RpcResponseHandler(this, classInterface, implementation));
    }

    @Override
    public <T> T createRpcProxy(final Class<T> classInterface, final IRpcTarget target)
    {
        //noinspection unchecked
        return (T) this.rpcProxyCache.get(classInterface, target);
    }

    @Override
    public RpcObjectDescription getObjectDescription(final Class<?> classInterface)
    {
        return this.descriptionCache.computeIfAbsent(classInterface, RpcObjectDescription::new);
    }

    public RpcResponseLock createLockForRequest(final int requestId)
    {
        final RpcResponseLock lock = new RpcResponseLock();
        synchronized (this.locks)
        {
            this.locks.put(requestId, lock);
        }
        return lock;
    }

    public void removeLock(final int requestId)
    {
        synchronized (this.locks)
        {
            this.locks.remove(requestId);
        }
    }

    public void publishInvokeMessage(final String invokeChannel, final RpcInvokeMessage rpcInvokeMessage)
    {
        final byte[] serializedMessage = this.msgPack.serialize(RpcInvokeMessage.class, rpcInvokeMessage);
        this.storageConnector.getRedis().publish(invokeChannel, serializedMessage);
    }

    public void sendResponse(final String target, final Integer requestId, final Object response)
    {
        final RpcResponseMessage responseMessage = new RpcResponseMessage(requestId, response);
        this.storageConnector.getRedis().publish("rpc:" + target + ":response", this.msgPack.serialize(RpcResponseMessage.class, responseMessage));
    }

    private void handleMethodInvocation(final String channel, final byte[] bytes)
    {
        final RpcInvokeMessage invokeMessage = this.msgPack.deserialize(RpcInvokeMessage.class, bytes);
        final RpcResponseHandler handler = this.responseHandlerMap.get(invokeMessage.getClassId());
        if (handler != null)
        {
            handler.handleInvoke(invokeMessage);
            return;
        }
        this.sendResponse(invokeMessage.getSender(), invokeMessage.getRequestId(), new RpcExceptionInfo(new RpcUnimplementedException()));
    }

    private void handleResponse(final String channel, final byte[] bytes)
    {
        final RpcResponseMessage responseMessage = this.msgPack.deserialize(RpcResponseMessage.class, bytes);

        final RpcResponseLock lock;
        synchronized (this.locks)
        {
            lock = this.locks.remove(responseMessage.getRequestId());
        }

        if (lock == null)
        {
            // It could happen when timeout happened and lock has been removed by RpcInvocationHandler.
            // In that case just ignore response.
            log.warn("Received RPC response but lock was null. Response: {}", responseMessage);
            return;
        }

        lock.provideResponse(responseMessage.getResponse());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
