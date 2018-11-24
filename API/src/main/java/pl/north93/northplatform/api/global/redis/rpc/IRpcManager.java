package pl.north93.northplatform.api.global.redis.rpc;

public interface IRpcManager
{
    void addListeningContext(String id);

    void addRpcImplementation(Class<?> classInterface, Object implementation);

    <T> T createRpcProxy(Class<T> classInterface, IRpcTarget target);

    IRpcObjectDescription getObjectDescription(Class<?> classInterface);
}
