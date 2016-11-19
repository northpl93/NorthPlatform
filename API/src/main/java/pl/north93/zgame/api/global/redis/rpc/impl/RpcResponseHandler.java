package pl.north93.zgame.api.global.redis.rpc.impl;

import java.lang.invoke.MethodHandle;
import java.util.logging.Level;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.redis.rpc.impl.messaging.RpcExceptionInfo;
import pl.north93.zgame.api.global.redis.rpc.impl.messaging.RpcInvokeMessage;
import pl.north93.zgame.api.global.redis.rpc.impl.messaging.RpcResponseMessage;
import redis.clients.jedis.Jedis;

public class RpcResponseHandler
{
    private final RpcObjectDescription objectDescription;
    private final Object               implementation;

    public RpcResponseHandler(final Class<?> classInterface, final Object implementation)
    {
        this.objectDescription = API.getRpcManager().getObjectDescription(classInterface);
        this.implementation = implementation;
    }

    public void handleInvoke(final RpcInvokeMessage rpcInvokeMessage)
    {
        final RpcMethodDescription methodDescription = this.objectDescription.getMethodDescription(rpcInvokeMessage.getMethodId());
        final MethodHandle methodHandle = methodDescription.getMethodHandle();

        final Object[] invocationArgs = new Object[rpcInvokeMessage.getArgs().length + 1];
        invocationArgs[0] = this.implementation;
        if (invocationArgs.length > 1)
        {
            final Object[] argsArray = rpcInvokeMessage.getArgs();
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
                    sendResponse(rpcInvokeMessage.getSender(), rpcInvokeMessage.getRequestId(), null);
                }
                else
                {
                    sendResponse(rpcInvokeMessage.getSender(), rpcInvokeMessage.getRequestId(), methodHandle.invokeWithArguments(invocationArgs));
                }
            }
            catch (final Throwable throwable)
            {
                sendResponse(rpcInvokeMessage.getSender(), rpcInvokeMessage.getRequestId(), new RpcExceptionInfo(throwable));
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

    /*default*/ static void sendResponse(final String target, final Integer requestId, final Object response)
    {
        try (final Jedis jedis = API.getJedis().getResource())
        {
            final RpcResponseMessage responseMessage = new RpcResponseMessage(requestId, response);
            jedis.publish(("rpc:" + target + ":response").getBytes(), API.getMessagePackTemplates().serialize(RpcResponseMessage.class, responseMessage));
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("objectDescription", this.objectDescription).append("implementation", this.implementation).toString();
    }
}
