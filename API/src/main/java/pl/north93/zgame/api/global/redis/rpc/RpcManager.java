package pl.north93.zgame.api.global.redis.rpc;

import pl.north93.zgame.api.global.redis.rpc.impl.RpcObjectDescription;

public interface RpcManager
{
    void addListeningContext(String id);

    void addRpcImplementation(Class<?> classInterface, Object implementation);

    <T> T createRpcProxy(Class<T> classInterface, RpcTarget target);

    RpcObjectDescription getObjectDescription(Class<?> classInterface);
}
