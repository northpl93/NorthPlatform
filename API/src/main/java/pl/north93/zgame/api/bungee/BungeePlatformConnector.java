package pl.north93.zgame.api.bungee;

import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import pl.north93.zgame.api.global.PlatformConnector;

public class BungeePlatformConnector implements PlatformConnector
{
    private final Main bungeePlugin;

    public BungeePlatformConnector(final Main bungeePlugin)
    {
        this.bungeePlugin = bungeePlugin;
    }

    @Override
    public void stop()
    {
        ProxyServer.getInstance().stop();
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
