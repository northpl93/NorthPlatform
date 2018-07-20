package pl.north93.zgame.api.standalone;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.PlatformConnector;

@Slf4j
public class StandalonePlatformConnector implements PlatformConnector
{
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    @Override
    public void stop()
    {
        log.info("Standalone platform application will shutdown in 5 seconds...");
        this.executor.schedule(() ->
        {
            this.executor.shutdown();
            API.getApiCore().stopCore();
        }, 5, TimeUnit.SECONDS);
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
                log.error("Exception thrown in runnable", e);
            }
        };
    }
}
