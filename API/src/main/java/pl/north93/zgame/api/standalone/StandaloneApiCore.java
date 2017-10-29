package pl.north93.zgame.api.standalone;

import static pl.north93.zgame.api.global.utils.ConfigUtils.loadConfigFile;


import java.io.File;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.Platform;
import pl.north93.zgame.api.global.utils.NorthFormatter;
import pl.north93.zgame.api.standalone.cfg.EnvironmentCfg;

public class StandaloneApiCore extends ApiCore
{
    private final Logger logger = Logger.getLogger("North API");
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

        final ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new NorthFormatter());
        this.logger.setUseParentHandlers(false);
        this.logger.addHandler(handler);
    }

    @Override
    public Logger getLogger()
    {
        return this.logger;
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
        this.logger.info("Initialising standalone application");
        final String fileLoc = System.getProperty("northplatform.environmentFile", "environment.yml");
        this.environmentCfg = loadConfigFile(EnvironmentCfg.class, this.getFile(fileLoc));
        this.debug("Using environment file: " + fileLoc + " Loaded content: " + this.environmentCfg);
    }

    @Override
    protected void start()
    {
        this.logger.info("Starting standalone application");
    }

    @Override
    protected void stop()
    {
        this.logger.info("Stopping standalone application");
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
