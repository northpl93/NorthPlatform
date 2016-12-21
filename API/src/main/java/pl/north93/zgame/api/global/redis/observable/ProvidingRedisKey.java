package pl.north93.zgame.api.global.redis.observable;

@FunctionalInterface
public interface ProvidingRedisKey
{
    ObjectKey getKey();
}
