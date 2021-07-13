package pl.north93.northplatform.api.global.redis.rpc.impl;

import java.util.concurrent.TimeUnit;

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

        final Object[] argsArray = rpcInvokeMessage.getArgs();
        final Object[] invocationArgs = new Object[argsArray.length + 1];
        invocationArgs[0] = this.implementation;
        if (invocationArgs.length > 1)
        {
            System.arraycopy(argsArray, 0, invocationArgs, 1, argsArray.length);
        }

        final long start = System.nanoTime();
        if (methodDescription.isNeedsWaitForResponse())
        {
            try
            {
                if (methodDescription.isReturnsVoid())
                {
                    methodDescription.invokeWithArguments(invocationArgs);
                    this.rpcManager.sendResponse(rpcInvokeMessage.getSender(), rpcInvokeMessage.getRequestId(), null);
                }
                else
                {
                    final Object result = methodDescription.invokeWithArguments(invocationArgs);
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
                methodDescription.invokeWithArguments(invocationArgs);
            }
            catch (final Throwable throwable)
            {
                log.error("Something went wrong while executing RPC method. (response isn't sending so I log this here)", throwable);
            }
        }

        final long executionDuration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        if (executionDuration > methodDescription.getTimeout())
        {
            final String methodName = methodDescription.getName();
            log.warn("[RPC] Method {} took {}ms to invoke. It means that timeout occurred.", methodName, executionDuration);
        }
    }
}
