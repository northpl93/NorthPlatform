package pl.north93.zgame.api.global.data;

import static pl.north93.zgame.api.global.cfg.ConfigUtils.loadConfigFile;


import java.util.concurrent.TimeUnit;
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

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.cfg.ConnectionConfig;
import pl.north93.zgame.api.global.component.Component;

public class StorageConnector extends Component
{
    private RedisClient                             redisClient;
    private StatefulRedisConnection<String, byte[]> redisConnection;
    private MongoClient   mongoClient;
    private MongoDatabase mainDatabase;

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
        this.redisConnection  = this.redisClient.connect(StringByteRedisCodec.INSTANCE);

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
        this.redisClient.shutdown();
        this.mongoClient.close();
    }

    /**
     * Ta metoda zwraca obiekt RedisCommands.
     * Nie nalezy zamykac tego polaczenia, poniewaz jest ono publiczne.
     *
     * @return obiekt do gadania z redisem synchronicznie.
     */
    public RedisCommands<String, byte[]> getRedis()
    {
        return this.redisConnection.sync();
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
