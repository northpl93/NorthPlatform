package pl.north93.zgame.api.global.redis.rpc;

@FunctionalInterface
public interface IRpcTarget
{
    String getRpcChannelName();
}
