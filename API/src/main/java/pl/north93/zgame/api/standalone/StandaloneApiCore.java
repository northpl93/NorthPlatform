package pl.north93.zgame.api.standalone;

import java.io.File;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.Platform;
import pl.north93.zgame.api.global.utils.NorthFormatter;

public class StandaloneApiCore extends ApiCore
{
    private final Logger        logger = Logger.getLogger("North API");
    private final StandaloneApp app;

    public StandaloneApiCore(final StandaloneApp standaloneApp)
    {
        super(Platform.STANDALONE, new StandalonePlatformConnector());
        this.app = standaloneApp;

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
        return this.app.getId();
    }

    @Override
    protected void start()
    {
        this.logger.info("Initialising standalone application");
        this.app.start(this);
    }

    @Override
    protected void stop()
    {
        this.app.stop();
    }

    @Override
    public File getFile(final String name)
    {
        return new File(name);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("app", this.app).toString();
    }
}
