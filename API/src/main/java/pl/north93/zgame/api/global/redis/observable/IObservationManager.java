package pl.north93.zgame.api.global.redis.observable;

public interface IObservationManager
{
    <T> Value<T> get(Class<T> clazz, String objectKey);

    <T> Value<T> get(Class<T> clazz, ObjectKey objectKey);

    <T> Value<T> get(Class<T> clazz, ProvidingRedisKey keyProvider);
}
