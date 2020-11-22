package pl.north93.northplatform.api.global.storage;

import java.io.File;
import java.time.Duration;
import java.util.function.Function;
import java.util.logging.Logger;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.UuidRepresentation;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.internal.CodecRegistryHelper;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.utils.ConfigUtils;
import pl.north93.serializer.mongodb.MongoDbCodec;
import pl.north93.serializer.mongodb.MongoDbSerializationFormat;
import pl.north93.serializer.platform.NorthSerializer;
import pl.north93.serializer.platform.template.impl.NorthSerializerImpl;

@Slf4j
public class StorageConnector extends Component
{
    private RedisClient redisClient;
    private StatefulRedisConnection<String, byte[]> redisConnection;
    private StatefulRedisConnection<String, byte[]> atomicallyConnection;
    private MongoClient mongoClient;
    private MongoDatabase mainDatabase;

    @Override
    protected void enableComponent()
    {
        final File configFile = this.getApiCore().getFile("connection.xml");
        final ConnectionConfig config = ConfigUtils.loadConfig(ConnectionConfig.class, configFile);

        this.redisClient = RedisClient.create(this.buildRedisUri(config));
        this.redisConnection  = this.redisClient.connect(StringByteRedisCodec.INSTANCE);
        this.atomicallyConnection = this.redisClient.connect(StringByteRedisCodec.INSTANCE);

        this.fixMongoLogger(Logger.getLogger("org.mongodb.driver.connection"));
        this.fixMongoLogger(Logger.getLogger("org.mongodb.driver.management"));
        this.fixMongoLogger(Logger.getLogger("org.mongodb.driver.cluster"));
        this.fixMongoLogger(Logger.getLogger("org.mongodb.driver.protocol.insert"));
        this.fixMongoLogger(Logger.getLogger("org.mongodb.driver.protocol.query"));
        this.fixMongoLogger(Logger.getLogger("org.mongodb.driver.protocol.update"));

        final MongoClientSettings.Builder builder = MongoClientSettings.builder();
        builder.applyConnectionString(new ConnectionString(config.getMongoDbConnect()));

        final NorthSerializerImpl<BsonWriter, BsonReader> serializer = new NorthSerializerImpl<>(new MongoDbSerializationFormat(), new NorthPlatformClassResolver());
        builder.codecRegistry(this.configureMongoCodecRegistry(serializer));

        builder.applyToConnectionPoolSettings(poolSettings ->
        {
            poolSettings.minSize(1);
            poolSettings.maxSize(10);
        });

        builder.uuidRepresentation(UuidRepresentation.JAVA_LEGACY);
        this.mongoClient = MongoClients.create(builder.build());

        this.mainDatabase = this.mongoClient.getDatabase(config.getMongoMainDatabase());
    }

    private RedisURI buildRedisUri(final ConnectionConfig config)
    {
        final RedisURI.Builder redisBuilder = RedisURI.Builder.redis(config.getRedisHost(), config.getRedisPort());

        final String redisPassword = config.getRedisPassword();
        if (StringUtils.isNotEmpty(redisPassword))
        {
            redisBuilder.withPassword(redisPassword);
        }

        final Duration timeout = Duration.ofMillis(config.getRedisTimeout());
        return redisBuilder.withTimeout(timeout).withDatabase(0).build();
    }

    private CodecRegistry configureMongoCodecRegistry(final NorthSerializer<BsonWriter, BsonReader> northSerializer)
    {
        final CodecRegistry defaultRegistry = MongoClientSettings.getDefaultCodecRegistry();
        final CodecRegistry codecRegistry = CodecRegistryHelper.createRegistry(defaultRegistry, UuidRepresentation.JAVA_LEGACY);

        return new CodecRegistry()
        {
            @Override
            public <T> Codec<T> get(final Class<T> clazz, final CodecRegistry registry)
            {
                try
                {
                    return codecRegistry.get(clazz);
                }
                catch (final CodecConfigurationException e)
                {
                    return new MongoDbCodec<>(northSerializer, clazz);
                }
            }

            @Override
            public <T> Codec<T> get(final Class<T> clazz)
            {
                return this.get(clazz, codecRegistry);
            }
        };
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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("mongoClient", this.mongoClient).append("mainDatabase", this.mainDatabase).toString();
    }
}
