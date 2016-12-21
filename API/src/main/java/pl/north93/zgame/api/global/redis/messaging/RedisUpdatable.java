package pl.north93.zgame.api.global.redis.messaging;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.data.StorageConnector;
import redis.clients.jedis.Jedis;

@Deprecated
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
        // TODO do it better
        final StorageConnector storageConnector = API.getApiCore().getComponentManager().getComponent("API.Database.StorageConnector");
        if (storageConnector.getStatus().isEnabled())
        {
            try (final Jedis jedis = storageConnector.getJedisPool().getResource())
            {
                jedis.set(this.getRedisKey().getBytes(), API.getMessagePackTemplates().serialize(this));
            }
        }
    }

    /**
     * Usuwa klucz tej instacji z Redisa.
     */
    default void delete()
    {
        // TODO do it better
        final StorageConnector storageConnector = API.getApiCore().getComponentManager().getComponent("API.Database.StorageConnector");
        if (storageConnector.getStatus().isEnabled())
        {
            try (final Jedis jedis = storageConnector.getJedisPool().getResource())
            {
                jedis.del(this.getRedisKey());
            }
        }
    }
}
