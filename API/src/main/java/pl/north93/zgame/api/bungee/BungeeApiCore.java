package pl.north93.zgame.api.bungee;

import static pl.north93.zgame.api.global.cfg.ConfigUtils.loadConfigFile;
import static pl.north93.zgame.api.global.redis.RedisKeys.PROXY_INSTANCE;


import java.io.File;
import java.util.logging.Logger;

import net.md_5.bungee.api.ProxyServer;
import pl.north93.zgame.api.bungee.cfg.ProxyInstanceConfig;
import pl.north93.zgame.api.bungee.connection.ConnectionManager;
import pl.north93.zgame.api.bungee.connection.NorthReconnectHandler;
import pl.north93.zgame.api.bungee.listeners.PingListener;
import pl.north93.zgame.api.bungee.listeners.PlayerListener;
import pl.north93.zgame.api.bungee.mods.IBungeeServersManager;
import pl.north93.zgame.api.bungee.mods.impl.BungeeServersManager;
import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.Platform;
import pl.north93.zgame.api.global.data.StorageConnector;
import pl.north93.zgame.api.global.messages.ProxyInstanceInfo;
import pl.north93.zgame.api.global.network.ProxyRpc;
import redis.clients.jedis.Jedis;

public class BungeeApiCore extends ApiCore
{
    private final Main            bungeePlugin;
    private IBungeeServersManager serversManager;
    private ConnectionManager     connectionManager;
    private ProxyInstanceConfig   config;

    public BungeeApiCore(final Main bungeePlugin)
    {
        super(Platform.BUNGEE, new BungeePlatformConnector(bungeePlugin));
        this.bungeePlugin = bungeePlugin;
    }

    @Override
    public Logger getLogger()
    {
        return this.bungeePlugin.getLogger();
    }

    @Override
    public String getId()
    {
        return PROXY_INSTANCE + this.config.getUniqueName();
    }

    @Override
    protected void init() throws Exception
    {
        this.config = loadConfigFile(ProxyInstanceConfig.class, this.getFile("proxy_instance.yml"));
    }

    @Override
    protected void start()
    {
        if (! ProxyServer.getInstance().getConfig().isIpForward())
        {
            this.getLogger().severe("Set ip_forward to true in config.yml");
            ProxyServer.getInstance().stop();
        }
        this.connectionManager = new ConnectionManager();
        ProxyServer.getInstance().setReconnectHandler(new NorthReconnectHandler(this));

        this.getRpcManager().addRpcImplementation(ProxyRpc.class, new ProxyRpcImpl(this));

        this.serversManager = new BungeeServersManager();
        this.serversManager.synchronizeServers();

        this.sendProxyInfo();

        this.getPlatformConnector().runTaskAsynchronously(this::sendProxyInfo, 300);

        this.bungeePlugin.getProxy().getPluginManager().registerListener(this.bungeePlugin, new PingListener());
        this.bungeePlugin.getProxy().getPluginManager().registerListener(this.bungeePlugin, new PlayerListener(this));
    }

    @Override
    protected void stop()
    {
        final StorageConnector storageConnector = this.getComponentManager().getComponent("API.Database.StorageConnector"); // TODO
        try (final Jedis jedis = storageConnector.getJedisPool().getResource())
        {
            jedis.del(this.getId());
        }
    }

    @Override
    public File getFile(final String name)
    {
        return new File(this.bungeePlugin.getDataFolder(), name);
    }

    public ConnectionManager getConnectionManager()
    {
        return this.connectionManager;
    }

    public ProxyInstanceConfig getProxyConfig()
    {
        return this.config;
    }

    public IBungeeServersManager getServersManager()
    {
        return this.serversManager;
    }

    public Main getBungeePlugin()
    {
        return this.bungeePlugin;
    }

    private void sendProxyInfo()
    {
        final StorageConnector storageConnector = API.getApiCore().getComponentManager().getComponent("API.Database.StorageConnector"); // TODO
        try (final Jedis jedis = storageConnector.getJedisPool().getResource())
        {
            final ProxyInstanceInfo proxyInstanceInfo = new ProxyInstanceInfo();

            proxyInstanceInfo.setId(this.config.getUniqueName());
            proxyInstanceInfo.setHostname(this.getHostName());
            proxyInstanceInfo.setOnlinePlayers(this.bungeePlugin.getProxy().getOnlineCount());

            jedis.set(this.getId().getBytes(), this.getMessagePackTemplates().serialize(proxyInstanceInfo));
        }
    }
}
