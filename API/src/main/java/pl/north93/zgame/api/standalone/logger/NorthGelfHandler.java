package pl.north93.zgame.api.standalone.logger;

import java.util.logging.Level;
import java.util.logging.Logger;

import biz.paluch.logging.gelf.jul.GelfLogHandler;

public class NorthGelfHandler extends GelfLogHandler
{
    private NorthGelfHandler()
    {
        super();

        this.setHost(System.getProperty("gelf.host"));
        this.setPort(Integer.getInteger("gelf.port", 12201));
        this.setOriginHost(System.getProperty("gelf.name"));

        this.setExtractStackTrace("true");
    }

    public static void setupHandler(final Logger logger)
    {
        if (System.getProperty("gelf.host") == null)
        {
            logger.log(Level.CONFIG, "Gelf handler is DISABLED!");
            return;
        }

        logger.addHandler(new NorthGelfHandler());
        logger.log(Level.CONFIG, "Enabled Gelf handler!");
    }
}
