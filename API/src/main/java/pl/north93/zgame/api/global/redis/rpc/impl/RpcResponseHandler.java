package pl.north93.zgame.api.global.redis.rpc.impl;

import java.lang.invoke.MethodHandle;
import java.util.logging.Level;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.redis.rpc.impl.messaging.RpcExceptionInfo;
import pl.north93.zgame.api.global.redis.rpc.impl.messaging.RpcInvokeMessage;

class RpcResponseHandler
{
    private final RpcManagerImpl       rpcManager;
    private final RpcObjectDescription objectDescription;
    private final Object               implementation;

    public RpcResponseHandler(final RpcManagerImpl rpcManager, final Class<?> classInterface, final Object implementation)
    {
        this.rpcManager = rpcManager;
        this.objectDescription = rpcManager.getObjectDescription(classInterface);
        this.implementation = implementation;
    }

    public void handleInvoke(final RpcInvokeMessage rpcInvokeMessage)
    {
        final RpcMethodDescription methodDescription = this.objectDescription.getMethodDescription(rpcInvokeMessage.getMethodId());
        final MethodHandle methodHandle = methodDescription.getMethodHandle();

        final Object[] argsArray = rpcInvokeMessage.getArgs();
        final Object[] invocationArgs = new Object[argsArray.length + 1];
        invocationArgs[0] = this.implementation;
        if (invocationArgs.length > 1)
        {
            System.arraycopy(argsArray, 0, invocationArgs, 1, argsArray.length);
        }

        final long start = System.currentTimeMillis();
        if (methodDescription.isNeedsWaitForResponse())
        {
            try
            {
                if (methodDescription.getMethod().getReturnType() == void.class)
                {
                    methodHandle.invokeWithArguments(invocationArgs);
                    this.rpcManager.sendResponse(rpcInvokeMessage.getSender(), rpcInvokeMessage.getRequestId(), null);
                }
                else
                {
                    this.rpcManager.sendResponse(rpcInvokeMessage.getSender(), rpcInvokeMessage.getRequestId(), methodHandle.invokeWithArguments(invocationArgs));
                }
            }
            catch (final Throwable throwable)
            {
                this.rpcManager.sendResponse(rpcInvokeMessage.getSender(), rpcInvokeMessage.getRequestId(), new RpcExceptionInfo(throwable));
                API.getLogger().log(Level.SEVERE, "Something went wrong while executing RPC method. (this exception is also send to remote)", throwable);
            }
        }
        else
        {
            try
            {
                methodHandle.invokeWithArguments(invocationArgs);
            }
            catch (final Throwable throwable)
            {
                API.getLogger().log(Level.SEVERE, "Something went wrong while executing RPC method. (response isn't sending so I log this here)", throwable);
            }
        }
        final long end = System.currentTimeMillis() - start;
        if (end > methodDescription.getTimeout())
        {
            API.getLogger().warning("[RPC] Method " + methodDescription.getMethod().getName() + " took " + end + "ms to invoke. It means that timeout occurred.");
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("objectDescription", this.objectDescription).append("implementation", this.implementation).toString();
    }
}
