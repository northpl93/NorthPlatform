package pl.north93.zgame.api.bungee;

import static pl.north93.zgame.api.global.cfg.ConfigUtils.loadConfigFile;
import static pl.north93.zgame.api.global.redis.RedisKeys.PROXY_INSTANCE;


import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import net.md_5.bungee.api.ProxyServer;
import pl.north93.zgame.api.bungee.cfg.ProxyInstanceConfig;
import pl.north93.zgame.api.bungee.listeners.PingListener;
import pl.north93.zgame.api.bungee.listeners.PlayerListener;
import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.Platform;
import pl.north93.zgame.api.global.messages.ProxyInstanceInfo;
import pl.north93.zgame.api.global.network.ProxyRpc;
import redis.clients.jedis.Jedis;

public class BungeeApiCore extends ApiCore
{
    private final Main bungeePlugin;
    private ProxyInstanceConfig config;

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
    protected void start()
    {
        if (! ProxyServer.getInstance().getConfig().isIpForward())
        {
            this.getLogger().severe("Set ip_forward to true in config.yml");
            ProxyServer.getInstance().stop();
        }
        this.config = loadConfigFile(ProxyInstanceConfig.class, this.getFile("proxy_instance.yml"));
        this.getRpcManager().addRpcImplementation(ProxyRpc.class, new ProxyRpcImpl());
        this.sendProxyInfo();
        this.getPlatformConnector().runTaskAsynchronously(this::sendProxyInfo, 300);
        this.bungeePlugin.getProxy().getPluginManager().registerListener(this.bungeePlugin, new PingListener());
        this.bungeePlugin.getProxy().getPluginManager().registerListener(this.bungeePlugin, new PlayerListener(this));
    }

    @Override
    protected void stop()
    {
        try (final Jedis jedis = this.getJedis().getResource())
        {
            jedis.del(this.getId());
        }
    }

    @Override
    protected File getFile(final String name)
    {
        return new File(this.bungeePlugin.getDataFolder(), name);
    }

    public ProxyInstanceConfig getProxyConfig()
    {
        return this.config;
    }

    public Main getBungeePlugin()
    {
        return this.bungeePlugin;
    }

    private void sendProxyInfo()
    {
        try (final Jedis jedis = this.getJedis().getResource())
        {
            final ProxyInstanceInfo proxyInstanceInfo = new ProxyInstanceInfo();

            proxyInstanceInfo.setId(this.config.getUniqueName());
            try
            {
                proxyInstanceInfo.setHostname(InetAddress.getLocalHost().getHostName());
            }
            catch (final UnknownHostException e)
            {
                proxyInstanceInfo.setHostname("<unknown:UnknownHostException>");
            }
            proxyInstanceInfo.setOnlinePlayers(this.bungeePlugin.getProxy().getOnlineCount());

            jedis.set(this.getId().getBytes(), this.getMessagePackTemplates().serialize(proxyInstanceInfo));
        }
    }
}
