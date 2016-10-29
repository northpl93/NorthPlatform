package pl.north93.zgame.api.bungee;

import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
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
    public void kickAll()
    {
        ProxyServer.getInstance().getPlayers().forEach(ProxiedPlayer::disconnect);
    }

    @Override
    public void runTaskAsynchronously(final Runnable runnable)
    {
        this.bungeePlugin.getProxy().getScheduler().runAsync(this.bungeePlugin, runnable);
    }

    @Override
    public void runTaskAsynchronously(final Runnable runnable, final int ticks)
    {
        this.bungeePlugin.getProxy().getScheduler().schedule(this.bungeePlugin, runnable, 0, ticks / 20, TimeUnit.SECONDS);
    }
}
