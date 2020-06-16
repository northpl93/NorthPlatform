package pl.north93.northplatform.api.global.redis.rpc.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.redis.rpc.IRpcTarget;
import pl.north93.northplatform.api.global.redis.rpc.exceptions.RpcRemoteException;
import pl.north93.northplatform.api.global.redis.rpc.exceptions.RpcTimeoutException;
import pl.north93.northplatform.api.global.redis.rpc.impl.messaging.RpcExceptionInfo;
import pl.north93.northplatform.api.global.redis.rpc.impl.messaging.RpcInvokeMessage;

class RpcInvocationHandler implements InvocationHandler
{
    private static final AtomicInteger requestCounter = new AtomicInteger(0);
    private final RpcManagerImpl rpcManager;
    private final RpcObjectDescription objectDescription;
    private final String invokeChannel;

    public RpcInvocationHandler(final RpcManagerImpl rpcManager, final Class<?> classInterface, final IRpcTarget target)
    {
        this.rpcManager = rpcManager;
        this.objectDescription = rpcManager.getObjectDescription(classInterface);
        this.invokeChannel = target.getRpcChannelName() + ":invoke";
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
    {
        final RpcMethodDescription methodDescription = this.objectDescription.getMethodDescription(method);

        final int requestId = requestCounter.getAndIncrement();
        final boolean needsWaitForResponse = methodDescription.isNeedsWaitForResponse();

        final RpcInvokeMessage rpcInvokeMessage = this.constructInvokeMessage(methodDescription, requestId, args);
        if (! needsWaitForResponse)
        {
            this.rpcManager.publishInvokeMessage(this.invokeChannel, rpcInvokeMessage);
            return null;
        }

        final RpcResponseLock lock = this.rpcManager.createLockForRequest(requestId);
        this.rpcManager.publishInvokeMessage(this.invokeChannel, rpcInvokeMessage);

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

    private RpcInvokeMessage constructInvokeMessage(final RpcMethodDescription methodDescription, final int requestId, final Object[] args)
    {
        final String senderId = this.rpcManager.getApiCore().getId();
        final int classId = this.objectDescription.getClassId();
        final int methodId = methodDescription.getId();

        return new RpcInvokeMessage(senderId, classId, requestId, methodId, args == null ? ArrayUtils.EMPTY_OBJECT_ARRAY : args);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("objectDescription", this.objectDescription).append("invokeChannel", new String(this.invokeChannel)).toString();
    }
}
