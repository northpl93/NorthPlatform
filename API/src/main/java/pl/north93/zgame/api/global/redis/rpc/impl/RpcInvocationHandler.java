package pl.north93.zgame.api.global.redis.rpc.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.redis.rpc.IRpcTarget;
import pl.north93.zgame.api.global.redis.rpc.exceptions.RpcRemoteException;
import pl.north93.zgame.api.global.redis.rpc.exceptions.RpcTimeoutException;
import pl.north93.zgame.api.global.redis.rpc.impl.messaging.RpcExceptionInfo;
import pl.north93.zgame.api.global.redis.rpc.impl.messaging.RpcInvokeMessage;
import redis.clients.jedis.Jedis;

class RpcInvocationHandler implements InvocationHandler
{
    private static final Object[]      EMPTY_ARRAY    = new Object[0];
    private static final AtomicInteger requestCounter = new AtomicInteger(0);
    private final RpcManagerImpl       rpcManager;
    private final RpcObjectDescription objectDescription;
    private final byte[]               invokeChannel;

    public RpcInvocationHandler(final RpcManagerImpl rpcManager, final Class<?> classInterface, final IRpcTarget target)
    {
        this.rpcManager = rpcManager;
        this.objectDescription = rpcManager.getObjectDescription(classInterface);
        this.invokeChannel = (target.getRpcChannelName() + ":invoke").getBytes();
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
    {
        final RpcMethodDescription methodDescription = this.objectDescription.getMethodDescription(method);

        final int methodId = methodDescription.getId();
        final int requestId = requestCounter.getAndIncrement();
        final boolean needsWaitForResponse = methodDescription.isNeedsWaitForResponse();

        final RpcResponseLock lock = needsWaitForResponse ? this.rpcManager.createFor(requestId) : null;

        final RpcInvokeMessage rpcInvokeMessage = new RpcInvokeMessage(API.getApiCore().getId(), this.objectDescription.getClassId(), requestId, methodId, args == null ? EMPTY_ARRAY : args);
        try (final Jedis jedis = this.rpcManager.getJedisPool().getResource())
        {
            jedis.publish(this.invokeChannel, this.rpcManager.getMsgPack().serialize(rpcInvokeMessage));
        }

        if (! needsWaitForResponse)
        {
            return null; // Nie jest wymagana odpowiedz, idziemy sobie
        }

        final Object response;
        try
        {
            response = lock.getResponse(methodDescription.getTimeout());
        }
        catch (final RpcTimeoutException e)
        {
            this.rpcManager.removeLock(requestId); // timeout happened so we will remove that lock
            throw new RuntimeException(e);
        }

        if (response instanceof RpcExceptionInfo)
        {
            throw new RpcRemoteException((RpcExceptionInfo) response);
        }
        return response;
    }
}
