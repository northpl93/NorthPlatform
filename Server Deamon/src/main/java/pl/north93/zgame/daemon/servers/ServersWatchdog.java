package pl.north93.zgame.daemon.servers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.API;

public class ServersWatchdog extends Thread
{
    private final List<ServerConsole> consoles;
    private       boolean             isRunning;

    public ServersWatchdog()
    {
        super("Servers Watchdog");
        this.isRunning = true;
        this.consoles = new ArrayList<>(64);
    }

    public void addConsole(final ServerConsole console)
    {
        API.debug("[WatchDog] New console added to watchdog: " + console);
        this.consoles.add(console);
    }

    public void safeStop()
    {
        API.debug("[WatchDog] safeStop");
        this.isRunning = false;
    }

    @Override
    public void run()
    {
        try
        {
            while (this.isRunning)
            {
                final Iterator<ServerConsole> consoleIterator = this.consoles.iterator();
                boolean any = false;

                while (consoleIterator.hasNext())
                {
                    final ServerConsole console = consoleIterator.next();
                    if (! console.getServerProcess().isAlive())
                    {
                        API.debug("[WatchDog] Removing console: " + console + " because isAlive() returned false");
                        console.serverProcessStopped();
                        consoleIterator.remove();
                        continue;
                    }
                    if (console.doReadCycle())
                    {
                        any = true;
                    }
                }
                if (! any)
                {
                    Thread.sleep(1000);
                }
            }
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("consoles", this.consoles).append("isRunning", this.isRunning).toString();
    }
}
