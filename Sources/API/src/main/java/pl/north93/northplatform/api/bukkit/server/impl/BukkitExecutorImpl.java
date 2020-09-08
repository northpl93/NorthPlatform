package pl.north93.northplatform.api.bukkit.server.impl;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.server.v1_12_R1.MinecraftServer;

import org.bukkit.Bukkit;

import pl.north93.northplatform.api.bukkit.BukkitApiCore;
import pl.north93.northplatform.api.bukkit.server.IBukkitExecutor;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;

class BukkitExecutorImpl implements IBukkitExecutor
{
    private final BukkitApiCore apiCore;

    @Bean
    private BukkitExecutorImpl(final BukkitApiCore apiCore)
    {
        this.apiCore = apiCore;
    }

    @Override
    public void sync(final Runnable runnable)
    {
        Bukkit.getScheduler().runTask(this.apiCore.getPluginMain(), runnable);
    }

    @Override
    public void async(final Runnable runnable)
    {
        Bukkit.getScheduler().runTaskAsynchronously(this.apiCore.getPluginMain(), runnable);
    }

    @Override
    public void syncLater(final int ticks, final Runnable runnable)
    {
        Bukkit.getScheduler().runTaskLater(this.apiCore.getPluginMain(), runnable, ticks);
    }

    @Override
    public void asyncLater(final int ticks, final Runnable runnable)
    {
        Bukkit.getScheduler().runTaskLaterAsynchronously(this.apiCore.getPluginMain(), runnable, ticks);
    }

    @Override
    public <T> void mixed(final Supplier<T> async, final Consumer<T> synced)
    {
        this.async(() ->
        {
            final T t = async.get();
            if (t != null)
            {
                this.sync(() -> synced.accept(t));
            }
        });
    }

    @Override
    public void syncTimer(final int every, final Runnable runnable)
    {
        Bukkit.getScheduler().runTaskTimer(this.apiCore.getPluginMain(), runnable, every, every);
    }

    @Override
    public void asyncTimer(final int every, final Runnable runnable)
    {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this.apiCore.getPluginMain(), runnable, every, every);
    }

    @Override
    public void inMainThread(final Runnable runnable)
    {
        if (MinecraftServer.getServer().isMainThread())
        {
            runnable.run();
        }
        else
        {
            this.sync(runnable);
        }
    }
}
