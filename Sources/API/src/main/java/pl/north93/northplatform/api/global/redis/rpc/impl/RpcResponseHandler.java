package pl.north93.northplatform.api.global.redis.rpc.impl;

import java.lang.invoke.MethodHandle;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.redis.rpc.impl.messaging.RpcExceptionInfo;
import pl.north93.northplatform.api.global.redis.rpc.impl.messaging.RpcInvokeMessage;

@Slf4j
@ToString(of = {"objectDescription", "implementation"})
class RpcResponseHandler
{
    private final RpcManagerImpl rpcManager;
    private final RpcObjectDescription objectDescription;
    private final Object implementation;

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
                    final Object result = methodHandle.invokeWithArguments(invocationArgs);
                    this.rpcManager.sendResponse(rpcInvokeMessage.getSender(), rpcInvokeMessage.getRequestId(), result);
                }
            }
            catch (final Throwable throwable)
            {
                this.rpcManager.sendResponse(rpcInvokeMessage.getSender(), rpcInvokeMessage.getRequestId(), new RpcExceptionInfo(throwable));
                log.error("Something went wrong while executing RPC method. (this exception is also send to remote)", throwable);
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
                log.error("Something went wrong while executing RPC method. (response isn't sending so I log this here)", throwable);
            }
        }
        final long end = System.currentTimeMillis() - start;
        if (end > methodDescription.getTimeout())
        {
            log.warn("[RPC] Method {} took {}ms to invoke. It means that timeout occurred.", methodDescription.getMethod().getName(), end);
        }
    }
}
