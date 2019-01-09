package pl.north93.northplatform.daemon.event;

import pl.north93.northplatform.daemon.servers.LocalServerConsole;
import pl.north93.northplatform.daemon.servers.LocalServerInstance;

public class ServerExitedEvent
{
    private final LocalServerInstance instance;
    private final LocalServerConsole  console;

    public ServerExitedEvent(final LocalServerConsole console)
    {
        this.instance = console.getInstance();
        this.console = console;
    }

    public LocalServerInstance getInstance()
    {
        return this.instance;
    }

    public LocalServerConsole getConsole()
    {
        return this.console;
    }
}
