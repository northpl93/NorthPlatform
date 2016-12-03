package pl.north93.zgame.api.global.data;

import static pl.north93.zgame.api.global.cfg.ConfigUtils.loadConfigFile;


import java.util.logging.Logger;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.cfg.ConnectionConfig;
import pl.north93.zgame.api.global.component.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class StorageConnector extends Component
{
    private ConnectionConfig connectionConfig;
    private JedisPool        pool;
    private MongoClient      mongoClient;
    private MongoDatabase    mainDatabase;

    @Override
    protected void enableComponent()
    {
        this.connectionConfig = loadConfigFile(ConnectionConfig.class, this.getApiCore().getFile("connection.yml"));

        this.pool = new JedisPool(new JedisPoolConfig(), this.connectionConfig.getRedisHost(), this.connectionConfig.getRedisPort(), this.connectionConfig.getRedisTimeout(), this.connectionConfig.getRedisPassword());
        this.pool.getResource().ping(); // check connection

        this.fixMongoLogger(Logger.getLogger("org.mongodb.driver.connection"));
        this.fixMongoLogger(Logger.getLogger("org.mongodb.driver.management"));
        this.fixMongoLogger(Logger.getLogger("org.mongodb.driver.cluster"));
        this.fixMongoLogger(Logger.getLogger("org.mongodb.driver.protocol.insert"));
        this.fixMongoLogger(Logger.getLogger("org.mongodb.driver.protocol.query"));
        this.fixMongoLogger(Logger.getLogger("org.mongodb.driver.protocol.update"));

        this.mongoClient = new MongoClient(new MongoClientURI(this.connectionConfig.getMongoDbConnect()));
        this.mainDatabase = this.mongoClient.getDatabase(this.connectionConfig.getMongoMainDatabase());
    }

    private void fixMongoLogger(final Logger logger)
    {
        logger.setParent(API.getLogger());
    }

    @Override
    protected void disableComponent()
    {
        this.pool.close();
        this.mongoClient.close();
    }

    public JedisPool getJedisPool()
    {
        return this.pool;
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("connectionConfig", this.connectionConfig).append("pool", this.pool).append("mongoClient", this.mongoClient).append("mainDatabase", this.mainDatabase).toString();
    }
}
