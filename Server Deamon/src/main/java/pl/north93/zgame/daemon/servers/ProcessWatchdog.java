package pl.north93.zgame.daemon.servers;

import com.google.common.eventbus.EventBus;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.component.annotations.bean.Named;
import pl.north93.zgame.daemon.event.ServerExitedEvent;

public class ProcessWatchdog implements Runnable
{
    @Inject @Named("daemon")
    private EventBus eventBus;
    @Inject
    private LocalServersManager localServersManager;

    @Override
    public void run()
    {
        // getInstances zwraca kopie wiec nie musimy sie martwic o problemy z edycja listy podczas iteracji
        for (final LocalServerInstance instance : this.localServersManager.getInstances())
        {
            final LocalServerConsole console = instance.getConsole();
            if (console.getProcess().isAlive())
            {
                continue;
            }

            this.eventBus.post(new ServerExitedEvent(console));
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
