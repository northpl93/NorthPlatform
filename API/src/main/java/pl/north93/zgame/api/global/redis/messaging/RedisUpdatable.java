package pl.north93.zgame.api.global.redis.messaging;

import pl.north93.zgame.api.global.API;
import redis.clients.jedis.Jedis;

public interface RedisUpdatable
{
    /**
     * Zwraca nazwę klucza w Redisie gdzie przechowywana jest ta instancja.
     *
     * @return nazwa klucza Redisa.
     */
    String getRedisKey();

    /**
     * Serializuje tą instancję i wysyła do Redisa.
     */
    default void sendUpdate()
    {
        try (final Jedis jedis = API.getJedis().getResource())
        {
            jedis.set(this.getRedisKey().getBytes(), API.getMessagePackTemplates().serialize(this));
        }
    }

    /**
     * Usuwa klucz tej instacji z Redisa.
     */
    default void delete()
    {
        try (final Jedis jedis = API.getJedis().getResource())
        {
            jedis.del(this.getRedisKey());
        }
    }
}
