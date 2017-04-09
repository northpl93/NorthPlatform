package pl.north93.zgame.api.global.data;

import static com.lambdaworks.redis.support.ConnectionPoolSupport.createGenericObjectPool;

import static pl.north93.zgame.api.global.cfg.ConfigUtils.loadConfigFile;


import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Logger;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.cfg.ConnectionConfig;
import pl.north93.zgame.api.global.component.Component;

public class StorageConnector extends Component
{
    private RedisClient       redisClient;
    private GenericObjectPool redisPool;
    private MongoClient       mongoClient;
    private MongoDatabase     mainDatabase;

    @Override
    protected void enableComponent()
    {
        final ConnectionConfig config = loadConfigFile(ConnectionConfig.class, this.getApiCore().getFile("connection.yml"));

        final RedisURI connectionUri = RedisURI.Builder.redis(config.getRedisHost(), config.getRedisPort())
                                               .withPassword(config.getRedisPassword())
                                               .withTimeout(config.getRedisTimeout(), TimeUnit.MILLISECONDS)
                                               .withDatabase(0)
                                               .build();
        this.redisClient = RedisClient.create(connectionUri);
        final Supplier<StatefulRedisConnection<String, byte[]>> supplier = () -> this.redisClient.connect(StringByteRedisCodec.INSTANCE);
        this.redisPool = createGenericObjectPool(supplier, new GenericObjectPoolConfig());

        this.fixMongoLogger(Logger.getLogger("org.mongodb.driver.connection"));
        this.fixMongoLogger(Logger.getLogger("org.mongodb.driver.management"));
        this.fixMongoLogger(Logger.getLogger("org.mongodb.driver.cluster"));
        this.fixMongoLogger(Logger.getLogger("org.mongodb.driver.protocol.insert"));
        this.fixMongoLogger(Logger.getLogger("org.mongodb.driver.protocol.query"));
        this.fixMongoLogger(Logger.getLogger("org.mongodb.driver.protocol.update"));

        this.mongoClient = new MongoClient(new MongoClientURI(config.getMongoDbConnect()));
        this.mainDatabase = this.mongoClient.getDatabase(config.getMongoMainDatabase());
    }

    private void fixMongoLogger(final Logger logger)
    {
        logger.setParent(API.getLogger());
    }

    @Override
    protected void disableComponent()
    {
        this.redisPool.close();
        this.redisClient.shutdown();
        this.mongoClient.close();
    }

    /**
     * Ta metoda zwraca obiekt RedisCommands pobrany z poola połączeń.
     * ZAWSZE po skończeniu używania go należy użyć metody close.
     *
     * Zalecam używanie tej metody w try/catch-with-resources
     * try (final RedisCommands<String, byte[]> redis = storageConnector.getRedis()) {
     *     // kod
     * }
     *
     * @return obiekt do gadania z redisem synchronicznie.
     */
    public RedisCommands<String, byte[]> getRedis()
    {
        final StatefulRedisConnection<String, byte[]> conn;
        try
        {
            //noinspection unchecked
            conn = (StatefulRedisConnection<String, byte[]>) this.redisPool.borrowObject();
        }
        catch (final Exception e)
        {
            throw new RuntimeException("Failed to borrow Redis connection from the pool.", e);
        }
        return conn.sync();
    }

    public RedisClient getRedisClient()
    {
        return this.redisClient;
    }

    public MongoClient getMongoClient()
    {
        return this.mongoClient;
    }

    public MongoDatabase getMainDatabase()
    {
        return this.mainDatabase;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("mongoClient", this.mongoClient).append("mainDatabase", this.mainDatabase).toString();
    }
}
