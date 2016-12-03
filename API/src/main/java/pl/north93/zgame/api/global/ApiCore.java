package pl.north93.zgame.api.global;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import pl.north93.zgame.api.global.component.IComponentManager;
import pl.north93.zgame.api.global.component.annotations.ProvidesComponent;
import pl.north93.zgame.api.global.component.impl.ComponentManagerImpl;
import pl.north93.zgame.api.global.data.PlayersDao;
import pl.north93.zgame.api.global.data.StorageConnector;
import pl.north93.zgame.api.global.data.UsernameCache;
import pl.north93.zgame.api.global.exceptions.SingletonException;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.permissions.PermissionsManager;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.api.global.redis.messaging.impl.TemplateManagerImpl;
import pl.north93.zgame.api.global.redis.rpc.RpcManager;
import pl.north93.zgame.api.global.redis.rpc.impl.RpcManagerImpl;
import pl.north93.zgame.api.global.redis.subscriber.RedisSubscriber;
import pl.north93.zgame.api.global.redis.subscriber.RedisSubscriberImpl;

public abstract class ApiCore
{
    private final IComponentManager  componentManager;
    private final Platform           platform;
    private final PlatformConnector  connector;
    private final TemplateManager    messagePackTemplates;
    private final RedisSubscriber    redisSubscriber;
    private final RpcManager         rpcManager;
    // - - - //
    private       StorageConnector   storageConnector;
    private       UsernameCache      usernameCache;
    private       INetworkManager    networkManager;
    private       PermissionsManager permissionsManager;
    private final PlayersDao         playersDao; // todo extract component

    public ApiCore(final Platform platform, final PlatformConnector platformConnector)
    {
        this.platform = platform;
        this.connector = platformConnector;
        this.componentManager = new ComponentManagerImpl(this);
        this.messagePackTemplates = new TemplateManagerImpl();
        this.redisSubscriber = new RedisSubscriberImpl();
        this.rpcManager = new RpcManagerImpl();
        this.playersDao = new PlayersDao();
        try
        {
            API.setApiCore(this);
        }
        catch (final SingletonException e)
        {
            throw new RuntimeException("You can't create more than one ApiCore class instances.", e);
        }
    }

    public final Platform getPlatform()
    {
        return this.platform;
    }

    public PlatformConnector getPlatformConnector()
    {
        return this.connector;
    }

    public final void startCore()
    {
        this.getLogger().info("Starting North API Core.");

        this.componentManager.doComponentScan(this.getClass().getClassLoader()); // scan for builtin API components
        this.componentManager.injectComponents(this.messagePackTemplates, this.redisSubscriber, this.rpcManager); // inject base API components
        this.componentManager.enableAllComponents(); // enable all components
        this.componentManager.setAutoEnable(true); // auto enable all newly discovered components

        this.storageConnector = this.componentManager.getComponent("API.Database.StorageConnector");
        this.usernameCache = this.componentManager.getComponent("API.MinecraftNetwork.UsernameCache");
        this.networkManager = this.componentManager.getComponent("API.MinecraftNetwork.NetworkManager");
        this.permissionsManager = this.componentManager.getComponent("API.MinecraftNetwork.PermissionsManager");

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
        this.componentManager.disableAllComponents();
        this.getLogger().info("North API Core stopped.");
    }

    public PlayersDao getPlayersDao()
    {
        return this.playersDao;
    }

    @ProvidesComponent
    public INetworkManager getNetworkManager()
    {
        return this.networkManager;
    }

    @ProvidesComponent
    public PermissionsManager getPermissionsManager()
    {
        return this.permissionsManager;
    }

    @ProvidesComponent
    public UsernameCache getUsernameCache()
    {
        return this.usernameCache;
    }

    @ProvidesComponent
    public TemplateManager getMessagePackTemplates()
    {
        return this.messagePackTemplates;
    }

    @ProvidesComponent
    public RedisSubscriber getRedisSubscriber()
    {
        return this.redisSubscriber;
    }

    @ProvidesComponent
    public RpcManager getRpcManager()
    {
        return this.rpcManager;
    }

    public IComponentManager getComponentManager()
    {
        return this.componentManager;
    }

    public void debug(final Object object)
    {
        this.getLogger().log(Level.FINE, object.toString());
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

    /**
     * @return file inside plugin's configuration directory
     */
    public abstract File getFile(final String name);

    protected abstract void start() throws Exception;

    protected abstract void stop() throws Exception;
}
