package pl.north93.zgame.api.standalone;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.PlatformConnector;

public class StandalonePlatformConnector implements PlatformConnector
{
    private final Logger logger = LoggerFactory.getLogger(StandalonePlatformConnector.class);
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    @Override
    public void stop()
    {
        this.logger.info("Standalone platform application will shutdown in 5 seconds...");
        this.executor.schedule(() ->
        {
            this.executor.shutdown();
            API.getApiCore().stopCore();
        }, 5, TimeUnit.SECONDS);
    }

    @Override
    public void kickAll()
    {
        this.logger.info("Received kick all request from network, but we doesn't do anything.");
    }

    @Override
    public void runTaskAsynchronously(final Runnable runnable)
    {
        this.executor.submit(this.wrapRunnable(runnable));
    }

    @Override
    public void runTaskAsynchronously(final Runnable runnable, final int ticks)
    {
        final int milliseconds = ticks * 50;
        this.executor.scheduleAtFixedRate(this.wrapRunnable(runnable), milliseconds, milliseconds, TimeUnit.MILLISECONDS);
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
