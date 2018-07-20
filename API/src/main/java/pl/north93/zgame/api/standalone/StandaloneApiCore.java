package pl.north93.zgame.api.standalone;

import java.io.File;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.Platform;
import pl.north93.zgame.api.global.utils.ConfigUtils;
import pl.north93.zgame.api.standalone.cfg.EnvironmentCfg;
import pl.north93.zgame.api.standalone.logger.NorthConsoleHandler;
import pl.north93.zgame.api.standalone.logger.NorthFormatter;
import pl.north93.zgame.api.standalone.logger.NorthGelfHandler;

public class StandaloneApiCore extends ApiCore
{
    protected EnvironmentCfg environmentCfg;

    public static void main(final String... args)
    {
        System.out.println("North API is running as standalone application");
        final StandaloneApiCore apiCore = new StandaloneApiCore();
        apiCore.startCore();
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            if (apiCore.getApiState().isDisabled())
            {
                return;
            }
            apiCore.stopCore();
        }));
    }

    public StandaloneApiCore()
    {
        super(Platform.STANDALONE, new StandalonePlatformConnector());
        this.setupLogger();
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
        consoleHandler.setLevel(this.isDebug() ? Level.FINE : Level.INFO);
        rootLogger.addHandler(consoleHandler);

        NorthGelfHandler.setupHandler(rootLogger);
    }

    @Override
    public String getId()
    {
        return this.environmentCfg.getId();
    }

    @Override
    public File getRootDirectory()
    {
        return new File(StandaloneApiCore.class.getProtectionDomain().getCodeSource().getLocation().getFile());
    }

    @Override
    protected void init() throws Exception
    {
        this.getApiLogger().info("Initialising standalone application");
        final String fileLoc = System.getProperty("northplatform.environmentFile", "environment.xml");
        this.environmentCfg = ConfigUtils.loadConfig(EnvironmentCfg.class, this.getFile(fileLoc));
        this.getApiLogger().debug("Using environment file: " + fileLoc + " Loaded content: " + this.environmentCfg);
    }

    @Override
    protected void start()
    {
        this.getApiLogger().info("Starting standalone application");
    }

    @Override
    protected void stop()
    {
        this.getApiLogger().info("Stopping standalone application");
    }

    @Override
    public File getFile(final String name)
    {
        return new File(name);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("environmentCfg", this.environmentCfg).toString();
    }
}
