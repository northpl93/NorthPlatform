package pl.north93.northplatform.api.standalone.logger;

import java.util.logging.ConsoleHandler;

public class NorthConsoleHandler extends ConsoleHandler
{
    public NorthConsoleHandler()
    {
        super();
        this.setOutputStream(System.out);
    }
}
