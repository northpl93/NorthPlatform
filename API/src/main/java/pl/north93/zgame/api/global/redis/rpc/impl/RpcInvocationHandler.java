package pl.north93.zgame.api.global.redis.rpc.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.redis.rpc.exceptions.RpcRemoteException;
import pl.north93.zgame.api.global.redis.rpc.RpcTarget;
import pl.north93.zgame.api.global.redis.rpc.impl.messaging.RpcExceptionInfo;
import pl.north93.zgame.api.global.redis.rpc.impl.messaging.RpcInvokeMessage;
import redis.clients.jedis.Jedis;

public class RpcInvocationHandler implements InvocationHandler
{
    private static final ArrayList<Object> emptyArrayList = new ArrayList<>(0); // used to sent empty arguments
    private static final AtomicInteger     requestCounter = new AtomicInteger(0);
    private final RpcManagerImpl           rpcManager;
    private final RpcObjectDescription     objectDescription;
    private final byte[]                   invokeChannel;

    public RpcInvocationHandler(final RpcManagerImpl rpcManager, final Class<?> classInterface, final RpcTarget target)
    {
        this.rpcManager = rpcManager;
        this.objectDescription = API.getRpcManager().getObjectDescription(classInterface);
        this.invokeChannel = (target.getRpcChannelName() + ":invoke").getBytes();
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
    {
        final RpcMethodDescription methodDescription = this.objectDescription.getMethodDescription(method);

        final int methodId = methodDescription.getId();
        final int requestId = requestCounter.getAndIncrement();

        final ArrayList<Object> arguments = args == null ? emptyArrayList : new ArrayList<>(Arrays.asList(args));
        final RpcInvokeMessage rpcInvokeMessage = new RpcInvokeMessage(API.getApiCore().getId(), this.objectDescription.getClassId(), requestId, methodId, arguments);
        try (final Jedis jedis = API.getJedis().getResource())
        {
            jedis.publish(this.invokeChannel, API.getMessagePackTemplates().serialize(rpcInvokeMessage));
        }

        if (! methodDescription.isNeedsWaitForResponse())
        {
            return null; // Nie jest wymagana odpowiedz, idziemy sobie
        }

        final Object response = this.rpcManager.createFor(requestId).getResponse(methodDescription.getTimeout());
        if (response instanceof RpcExceptionInfo)
        {
            throw new RpcRemoteException((RpcExceptionInfo) response);
        }
        return response;
    }
}
