package pl.north93.zgame.api.bungee;

import static pl.north93.zgame.api.global.redis.RedisKeys.PROXY_INSTANCE;


import java.io.File;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.PluginManager;
import pl.north93.zgame.api.bungee.cfg.ProxyInstanceConfig;
import pl.north93.zgame.api.bungee.connection.ConnectionManager;
import pl.north93.zgame.api.bungee.connection.NorthReconnectHandler;
import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.Platform;
import pl.north93.zgame.api.global.utils.ConfigUtils;
import pl.north93.zgame.api.standalone.logger.NorthGelfHandler;

public class BungeeApiCore extends ApiCore
{
    private final Main          bungeePlugin;
    private ConnectionManager   connectionManager;
    private ProxyInstanceConfig config;

    public BungeeApiCore(final Main bungeePlugin)
    {
        super(Platform.BUNGEE, new BungeePlatformConnector(bungeePlugin));
        this.bungeePlugin = bungeePlugin;
        this.setupLogger();
    }

    private void setupLogger()
    {
        final Logger bungeeLogger = ProxyServer.getInstance().getLogger();

        final Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setParent(bungeeLogger);
        for (final Handler handler : rootLogger.getHandlers())
        {
            rootLogger.removeHandler(handler);
        }
        rootLogger.setUseParentHandlers(true);

        NorthGelfHandler.setupHandler(bungeeLogger);
    }

    @Override
    public String getId()
    {
        return PROXY_INSTANCE + this.config.getUniqueName();
    }

    @Override
    public File getRootDirectory()
    {
        return new File(ProxyServer.class.getProtectionDomain().getCodeSource().getLocation().getFile());
    }

    @Override
    protected void init()
    {
        this.config = ConfigUtils.loadConfig(ProxyInstanceConfig.class, "proxy_instance.xml");
    }

    @Override
    protected void start()
    {
        if (! ProxyServer.getInstance().getConfig().isIpForward())
        {
            this.getApiLogger().error("Set ip_forward to true in bungee's config.yml");
            ProxyServer.getInstance().stop();
        }
        this.connectionManager = new ConnectionManager();
        ProxyServer.getInstance().setReconnectHandler(new NorthReconnectHandler(this));
    }

    @Override
    protected void stop()
    {
        // anulujemy wszystkie taski naszych komponentów bo w bungee moga sie wykonywac nawet po zatrzymaniu pluginu
        ProxyServer.getInstance().getScheduler().cancel(this.bungeePlugin);
    }

    @Override
    public File getFile(final String name)
    {
        return new File(this.bungeePlugin.getDataFolder(), name);
    }

    /**
     * Rejestruje podane instancje jako listenery BungeeCorda.
     *
     * @param listeners Listenery do zarejestrowania.
     */
    public void registerListeners(final Listener... listeners)
    {
        final PluginManager pluginManager = this.bungeePlugin.getProxy().getPluginManager();
        for (final Listener listener : listeners)
        {
            pluginManager.registerListener(this.bungeePlugin, listener);
        }
    }

    public <T extends Event> T callEvent(final T event)
    {
        return this.bungeePlugin.getProxy().getPluginManager().callEvent(event);
    }

    public ConnectionManager getConnectionManager()
    {
        return this.connectionManager;
    }

    public ProxyInstanceConfig getProxyConfig()
    {
        return this.config;
    }

    public Main getBungeePlugin()
    {
        return this.bungeePlugin;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("bungeePlugin", this.bungeePlugin).toString();
    }
}
