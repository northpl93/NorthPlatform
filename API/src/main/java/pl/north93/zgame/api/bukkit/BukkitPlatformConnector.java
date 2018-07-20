package pl.north93.zgame.api.bukkit;

import org.bukkit.Bukkit;

import pl.north93.zgame.api.global.PlatformConnector;

public class BukkitPlatformConnector implements PlatformConnector
{
    private final Main bukkitPlugin;

    public BukkitPlatformConnector(final Main bukkitPlugin)
    {
        this.bukkitPlugin = bukkitPlugin;
    }

    @Override
    public void stop()
    {
        Bukkit.shutdown();
    }

    @Override
    public void runTaskAsynchronously(final Runnable runnable)
    {
        this.bukkitPlugin.getServer().getScheduler().runTaskAsynchronously(this.bukkitPlugin, runnable);
    }

    @Override
    public void runTaskAsynchronously(final Runnable runnable, final int ticks)
    {
        this.bukkitPlugin.getServer().getScheduler().runTaskTimerAsynchronously(this.bukkitPlugin, runnable, 0, ticks);
    }
}
