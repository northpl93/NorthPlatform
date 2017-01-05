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
        API.getLogger().info("Standalone platform application will shutdown in 5 seconds...");
        try
        {
            synchronized (this)
            {
                this.wait(TimeUnit.SECONDS.toMillis(5));
            }
        }
        catch (final InterruptedException e)
        {
            e.printStackTrace();
        }
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
        this.executor.submit(this.wrapRunnable(runnable));
    }

    @Override
    public void runTaskAsynchronously(final Runnable runnable, final int ticks)
    {
        final int seconds = ticks / 20;
        this.executor.scheduleAtFixedRate(this.wrapRunnable(runnable), seconds, seconds, TimeUnit.SECONDS);
    }

    private Runnable wrapRunnable(final Runnable runnable) // exposes the stacktrace
    {
        return () ->
        {
            try
            {
                runnable.run();
            }
            catch (final Exception e)
            {
                e.printStackTrace();
            }
        };
    }
}
