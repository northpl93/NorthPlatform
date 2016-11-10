package pl.north93.zgame.api.global;

import static pl.north93.zgame.api.global.cfg.ConfigUtils.loadConfigFile;


import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.apache.commons.dbcp2.BasicDataSource;

import pl.north93.zgame.api.global.cfg.ConnectionConfig;
import pl.north93.zgame.api.global.data.PlayersDao;
import pl.north93.zgame.api.global.data.SqlTables;
import pl.north93.zgame.api.global.data.UsernameCache;
import pl.north93.zgame.api.global.exceptions.SingletonException;
import pl.north93.zgame.api.global.network.NetworkManager;
import pl.north93.zgame.api.global.permissions.PermissionsManager;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.api.global.redis.messaging.impl.TemplateFactoryImpl;
import pl.north93.zgame.api.global.redis.messaging.impl.TemplateManagerImpl;
import pl.north93.zgame.api.global.redis.rpc.RpcManager;
import pl.north93.zgame.api.global.redis.rpc.impl.RpcManagerImpl;
import pl.north93.zgame.api.global.redis.subscriber.RedisSubscriber;
import pl.north93.zgame.api.global.redis.subscriber.RedisSubscriberImpl;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public abstract class ApiCore
{
    private final TemplateManager    messagePackTemplates;
    private final Platform           platform;
    private final PlatformConnector  connector;
    private final RedisSubscriber    redisSubscriber;
    private final RpcManager         rpcManager;
    private final UsernameCache      usernameCache;
    private final NetworkManager     networkManager;
    private final PermissionsManager permissionsManager;
    private final PlayersDao         playersDao;
    private       ConnectionConfig   connectionConfig;
    private       BasicDataSource    mysqlDataSource;
    private       JedisPool          pool;

    {
        try
        {
            API.setApiCore(this);
        }
        catch (final SingletonException e)
        {
            throw new RuntimeException("You can't create more than one ApiCore class instances.", e);
        }
    }

    public ApiCore(final Platform platform, final PlatformConnector platformConnector)
    {
        this.platform = platform;
        this.connector = platformConnector;
        this.messagePackTemplates = new TemplateManagerImpl(new TemplateFactoryImpl());
        this.redisSubscriber = new RedisSubscriberImpl();
        this.rpcManager = new RpcManagerImpl();
        this.networkManager = new NetworkManager();
        this.permissionsManager = new PermissionsManager(this);
        this.usernameCache = new UsernameCache();
        this.playersDao = new PlayersDao();
    }

    public final Platform getPlatform()
    {
        return this.platform;
    }

    public PlatformConnector getPlatformConnector()
    {
        return this.connector;
    }

    public PermissionsManager getPermissionsManager()
    {
        return this.permissionsManager;
    }

    public final void startCore()
    {
        this.getLogger().info("Starting North API Core.");
        this.connectionConfig = loadConfigFile(ConnectionConfig.class, this.getFile("connection.yml"));
        if (System.getProperties().containsKey("northplatform.forcedebug"))
        {
            this.connectionConfig.setDebug(true);
        }
        this.pool = new JedisPool(new JedisPoolConfig(), this.connectionConfig.getRedisHost(), this.connectionConfig.getRedisPort(), this.connectionConfig.getRedisTimeout(), this.connectionConfig.getRedisPassword());
        {
            this.mysqlDataSource = new BasicDataSource();
            this.mysqlDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            this.mysqlDataSource.setUrl("jdbc:mysql://" + this.connectionConfig.getMysqlHost() + "/" + this.connectionConfig.getMysqlDatabase());
            this.mysqlDataSource.setUsername(this.connectionConfig.getMysqlUser());
            this.mysqlDataSource.setPassword(this.connectionConfig.getMysqlPassword());
            SqlTables.createTables();
        }
        this.networkManager.start();
        this.permissionsManager.synchronizeGroups();
        try
        {
            this.start();
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            this.getPlatformConnector().stop();
        }
        this.rpcManager.addListeningContext(this.getId());
        this.getLogger().info("Client id is " + this.getId());
    }

    public final void stopCore()
    {
        try
        {
            this.stop();
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
        this.redisSubscriber.unSubscribeAll();
        this.pool.destroy();
        try
        {
            this.mysqlDataSource.close();
        }
        catch (final SQLException e)
        {
            throw new RuntimeException("Failed to close MySQL pool", e);
        }
        this.getLogger().info("North API Core stopped.");
    }

    public PlayersDao getPlayersDao()
    {
        return this.playersDao;
    }

    public NetworkManager getNetworkManager()
    {
        return this.networkManager;
    }

    public UsernameCache getUsernameCache()
    {
        return this.usernameCache;
    }

    public TemplateManager getMessagePackTemplates()
    {
        return this.messagePackTemplates;
    }

    public JedisPool getJedis()
    {
        this.debug("Jedis pool accessed.");
        return this.pool;
    }

    public RedisSubscriber getRedisSubscriber()
    {
        return this.redisSubscriber;
    }

    public RpcManager getRpcManager()
    {
        return this.rpcManager;
    }

    public BasicDataSource getMysql()
    {
        return this.mysqlDataSource;
    }

    public void debug(final Object object)
    {
        if (! this.connectionConfig.isDebug())
        {
            return;
        }
        this.getLogger().info(object.toString());
    }

    public void runDebug(final Runnable runnable)
    {
        if (! this.connectionConfig.isDebug())
        {
            return;
        }
        runnable.run();
    }

    /**
     * Returns machine's network name.
     *
     * @return host name
     */
    public String getHostName()
    {
        try
        {
            return InetAddress.getLocalHost().getHostName();
        }
        catch (final UnknownHostException e)
        {
            return "<unknown:UnknownHostException>";
        }
    }

    public abstract Logger getLogger();

    public abstract String getId();

    protected abstract void start() throws Exception;

    protected abstract void stop() throws Exception;

    /**
     * @return file inside plugin's configuration directory
     */
    protected abstract File getFile(final String name);
}
