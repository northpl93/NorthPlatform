package pl.north93.northplatform.api.global.redis.observable;

@FunctionalInterface
public interface ProvidingRedisKey
{
    ObjectKey getKey();
}
