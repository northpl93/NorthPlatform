package pl.north93.northplatform.api.bungee;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import lombok.extern.slf4j.Slf4j;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import pl.north93.northplatform.api.bungee.cfg.ProxyInstanceConfig;
import pl.north93.northplatform.api.global.ApiCore;
import pl.north93.northplatform.api.global.HostConnector;
import pl.north93.northplatform.api.global.HostId;
import pl.north93.northplatform.api.global.redis.RedisKeys;
import pl.north93.northplatform.api.global.utils.ConfigUtils;
import pl.north93.northplatform.api.standalone.logger.NorthGelfHandler;

@Slf4j
public class BungeeHostConnector implements HostConnector
{
    public static final HostId BUNGEE_HOST = new HostId("bungee");
    private final Main bungeePlugin;
    private ProxyInstanceConfig config;

    public BungeeHostConnector(final Main bungeePlugin)
    {
        this.bungeePlugin = bungeePlugin;
    }

    @Override
    public String onPlatformInit(final ApiCore apiCore)
    {
        this.setupLogger();

        final File configFile = this.getFile("proxy_instance.xml");
        this.config = ConfigUtils.loadConfig(ProxyInstanceConfig.class, configFile);

        return RedisKeys.PROXY_INSTANCE + this.config.getUniqueName();
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
    public void onPlatformStart(final ApiCore apiCore)
    {
        if (! ProxyServer.getInstance().getConfig().isIpForward())
        {
            log.error("Set ip_forward to true in bungee's config.yml");
            ProxyServer.getInstance().stop();
        }
    }

    @Override
    public void onPlatformStop(final ApiCore apiCore)
    {
        // anulujemy wszystkie taski naszych komponentów bo w bungee moga sie wykonywac nawet po zatrzymaniu pluginu
        ProxyServer.getInstance().getScheduler().cancel(this.bungeePlugin);
    }

    @Override
    public File getRootDirectory()
    {
        return new File(ProxyServer.class.getProtectionDomain().getCodeSource().getLocation().getFile());
    }

    @Override
    public File getFile(final String name)
    {
        return new File(this.bungeePlugin.getDataFolder(), name);
    }

    @Override
    public void shutdownHost()
    {
        ProxyServer.getInstance().stop();
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

    public ProxyInstanceConfig getProxyConfig()
    {
        return this.config;
    }

    public Main getBungeePlugin()
    {
        return this.bungeePlugin;
    }

    @Override
    public void runTaskAsynchronously(final Runnable runnable)
    {
        final TaskScheduler scheduler = this.bungeePlugin.getProxy().getScheduler();
        scheduler.runAsync(this.bungeePlugin, runnable);
    }

    @Override
    public void runTaskAsynchronously(final Runnable runnable, final int ticks)
    {
        final TaskScheduler scheduler = this.bungeePlugin.getProxy().getScheduler();
        scheduler.schedule(this.bungeePlugin, runnable, 0, ticks * 50, TimeUnit.MILLISECONDS);
    }
}
