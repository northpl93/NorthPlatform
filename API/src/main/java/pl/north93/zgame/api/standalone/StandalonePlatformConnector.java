package pl.north93.zgame.api.standalone;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.PlatformConnector;

public class StandalonePlatformConnector implements PlatformConnector
{
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    @Override
    public void stop()
    {
        API.getLogger().info("Received stop request from network.");
        this.executor.shutdown();
        API.getApiCore().stopCore();
    }

    @Override
    public void kickAll()
    {
        API.getLogger().info("Received kick all request from network, but we doesn't do anything.");
    }

    @Override
    public void runTaskAsynchronously(final Runnable runnable)
    {
        this.executor.execute(runnable);
    }

    @Override
    public void runTaskAsynchronously(final Runnable runnable, final int ticks)
    {
        final int seconds = ticks / 20;
        this.executor.scheduleAtFixedRate(runnable, seconds, seconds, TimeUnit.SECONDS);
    }
}
