package pl.north93.zgame.api.global.redis.rpc.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;
import com.lambdaworks.redis.api.sync.RedisCommands;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.api.global.redis.rpc.IRpcTarget;
import pl.north93.zgame.api.global.redis.rpc.exceptions.RpcUnimplementedException;
import pl.north93.zgame.api.global.redis.rpc.impl.messaging.RpcExceptionInfo;
import pl.north93.zgame.api.global.redis.rpc.impl.messaging.RpcInvokeMessage;
import pl.north93.zgame.api.global.redis.rpc.impl.messaging.RpcResponseMessage;
import pl.north93.zgame.api.global.redis.subscriber.RedisSubscriber;
import pl.north93.zgame.api.global.storage.StorageConnector;

public class RpcManagerImpl extends Component implements IRpcManager
{
    @Inject
    private StorageConnector                        storageConnector;
    @Inject
    private RedisSubscriber                         redisSubscriber;
    @Inject
    private TemplateManager                         msgPack;
    private final RpcProxyCache                       rpcProxyCache      = new RpcProxyCache(this);
    private final IntObjectMap<RpcResponseHandler>    responseHandlerMap = new IntObjectHashMap<>();
    private final IntObjectMap<RpcResponseLock>       locks              = new IntObjectHashMap<>();
    private final Map<Class<?>, RpcObjectDescription> descriptionCache   = new ConcurrentHashMap<>();

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
            this.getApiCore().getLogger().warning("Tried to register listeningContext while RpcManager is disabled");
            return;
        }
        this.addListeningContext0(id);
    }

    private synchronized void addListeningContext0(final String id)
    {
        this.getApiCore().debug("addListeningContext0(" + id + ")");
        this.redisSubscriber.subscribe("rpc:" + id + ":invoke", this::handleMethodInvocation);
        this.redisSubscriber.subscribe("rpc:" + id + ":response", this::handleResponse);
    }

    @Override
    public synchronized void addRpcImplementation(final Class<?> classInterface, final Object implementation)
    {
        this.getApiCore().debug("addRpcImplementation(" + classInterface + ", " + implementation.getClass().getName() + ")");
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
        final RpcObjectDescription rpcObjectDescription = this.descriptionCache.get(classInterface);
        if (rpcObjectDescription == null)
        {
            final RpcObjectDescription newObjDesc = new RpcObjectDescription(classInterface);
            this.descriptionCache.put(classInterface, newObjDesc);
            return newObjDesc;
        }
        return rpcObjectDescription;
    }

    /*default*/ RpcResponseLock createFor(final int requestId)
    {
        final RpcResponseLock lock = new RpcResponseLock();
        synchronized (this.locks)
        {
            this.locks.put(requestId, lock);
        }
        return lock;
    }

    /*default*/ void removeLock(final int requestId)
    {
        synchronized (this.locks)
        {
            this.locks.remove(requestId);
        }
    }

    /*default*/ RedisCommands<String, byte[]> getJedisPool()
    {
        return this.storageConnector.getRedis();
    }

    /*default*/ TemplateManager getMsgPack()
    {
        return this.msgPack;
    };

    /*default*/ void sendResponse(final String target, final Integer requestId, final Object response)
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
            lock = this.locks.get(responseMessage.getRequestId());
        }
        if (lock == null)
        {
            this.getApiCore().getLogger().warning("Received RPC response but lock was null. Response:" + responseMessage);
            return; // Moze się wydarzyć, gdy nastąpi timeout i lock zostanie usunięty. W takim wypadku ignorujemy odpowiedź.
        }
        lock.provideResponse(responseMessage.getResponse());
        this.removeLock(responseMessage.getRequestId());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
