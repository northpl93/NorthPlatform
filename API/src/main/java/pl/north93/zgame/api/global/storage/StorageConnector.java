package pl.north93.zgame.api.global.storage;

import static pl.north93.zgame.api.global.utils.ConfigUtils.loadConfig;


import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import com.mongodb.Function;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.DefaultCreator;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.Component;

public class StorageConnector extends Component
{
    private RedisClient                             redisClient;
    private StatefulRedisConnection<String, byte[]> redisConnection;
    private StatefulRedisConnection<String, byte[]> atomicallyConnection;
    private MongoClient                             mongoClient;
    private MongoDatabase                           mainDatabase;
    private Morphia                                 morphia;
    private Datastore                               datastore;

    @Override
    protected void enableComponent()
    {
        final ConnectionConfig config = loadConfig(ConnectionConfig.class, "connection.xml");

        final RedisURI connectionUri = RedisURI.Builder.redis(config.getRedisHost(), config.getRedisPort())
                                               .withPassword(config.getRedisPassword())
                                               .withTimeout(config.getRedisTimeout(), TimeUnit.MILLISECONDS)
                                               .withDatabase(0)
                                               .build();
        this.redisClient = RedisClient.create(connectionUri);
        this.redisConnection  = this.redisClient.connect(StringByteRedisCodec.INSTANCE);
        this.atomicallyConnection = this.redisClient.connect(StringByteRedisCodec.INSTANCE);

        this.fixMongoLogger(Logger.getLogger("org.mongodb.driver.connection"));
        this.fixMongoLogger(Logger.getLogger("org.mongodb.driver.management"));
        this.fixMongoLogger(Logger.getLogger("org.mongodb.driver.cluster"));
        this.fixMongoLogger(Logger.getLogger("org.mongodb.driver.protocol.insert"));
        this.fixMongoLogger(Logger.getLogger("org.mongodb.driver.protocol.query"));
        this.fixMongoLogger(Logger.getLogger("org.mongodb.driver.protocol.update"));

        final MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
        builder.connectionsPerHost(10); // maksymalnie 10 polaczen. Default 100
        builder.minConnectionsPerHost(1); // minimum utrzymywane jedno polaczenie. Default 0
        this.mongoClient = new MongoClient(new MongoClientURI(config.getMongoDbConnect(), builder));

        this.mainDatabase = this.mongoClient.getDatabase(config.getMongoMainDatabase());

        this.morphia = new Morphia();
        this.morphia.getMapper().getOptions().setObjectFactory(new DefaultCreator()
        {
            @Override
            protected ClassLoader getClassLoaderForClass()
            {
                final ApiCore apiCore = StorageConnector.this.getApiCore();
                return apiCore.getComponentManager().getBossClassLoader();
            }
        });

        this.datastore = this.morphia.createDatastore(this.mongoClient, config.getMongoMainDatabase());
    }

    private void fixMongoLogger(final Logger logger)
    {
        //logger.setParent(API.getLogger()); // todo
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

    /**
     * Udostepnia drugie polaczenie redisa do rzeczy ktore musza byc przeprowadzane
     * atmowo, np. uzywanie multi.
     *
     * @param function Funkcja przyjmujaca klienta redisa i zwracajaca wynik.
     * @param <R> Typ wyniku.
     * @return Wynik zwrocony przez funkcje.
     */
    public synchronized <R> R redisAtomically(final Function<RedisCommands<String, byte[]>, R> function)
    {
        return function.apply(this.atomicallyConnection.sync());
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

    public Datastore getDatastore()
    {
        return this.datastore;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("mongoClient", this.mongoClient).append("mainDatabase", this.mainDatabase).toString();
    }
}
