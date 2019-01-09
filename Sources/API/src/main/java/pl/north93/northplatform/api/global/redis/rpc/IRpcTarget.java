package pl.north93.northplatform.api.global.redis.rpc;

@FunctionalInterface
public interface IRpcTarget
{
    String getRpcChannelName();
}
