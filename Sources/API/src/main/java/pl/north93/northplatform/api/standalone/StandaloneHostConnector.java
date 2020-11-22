package pl.north93.northplatform.api.standalone;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.ApiCore;
import pl.north93.northplatform.api.global.HostConnector;
import pl.north93.northplatform.api.global.utils.ConfigUtils;
import pl.north93.northplatform.api.standalone.cfg.EnvironmentCfg;
import pl.north93.northplatform.api.standalone.logger.NorthConsoleHandler;
import pl.north93.northplatform.api.standalone.logger.NorthFormatter;
import pl.north93.northplatform.api.standalone.logger.NorthGelfHandler;

@Slf4j
public class StandaloneHostConnector implements HostConnector
{
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    protected EnvironmentCfg environmentCfg;
    private ApiCore apiCore;

    @Override
    public String onPlatformInit(final ApiCore apiCore)
    {
        this.apiCore = apiCore;

        this.setupLogger();

        log.info("Initialising standalone application");
        final String fileLoc = System.getProperty("northplatform.environmentFile", "environment.xml");
        this.environmentCfg = ConfigUtils.loadConfig(EnvironmentCfg.class, this.getFile(fileLoc));
        log.debug("Using environment file: " + fileLoc + " Loaded content: " + this.environmentCfg);

        return this.environmentCfg.getId();
    }

    private void setupLogger()
    {
        final Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setLevel(Level.ALL);

        for (final Handler handler : rootLogger.getHandlers())
        {
            rootLogger.removeHandler(handler);
        }

        final StreamHandler consoleHandler = new NorthConsoleHandler();
        consoleHandler.setFormatter(new NorthFormatter());
        consoleHandler.setLevel(this.apiCore.isDebug() ? Level.FINE : Level.INFO);
        rootLogger.addHandler(consoleHandler);

        NorthGelfHandler.setupHandler(rootLogger);
    }

    @Override
    public void onPlatformStart(final ApiCore apiCore)
    {

    }

    @Override
    public void onPlatformStop(final ApiCore apiCore)
    {
        log.info("Stopping standalone platform connector");
        this.executor.shutdown();
    }

    @Override
    public File getRootDirectory()
    {
        return new File(StandaloneApiCore.class.getProtectionDomain().getCodeSource().getLocation().getFile());
    }

    @Override
    public File getFile(final String name)
    {
        return new File(name);
    }

    @Override
    public void shutdownHost()
    {
        System.exit(0);
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
