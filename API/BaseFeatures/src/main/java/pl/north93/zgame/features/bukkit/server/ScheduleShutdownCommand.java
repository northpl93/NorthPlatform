package pl.north93.zgame.features.bukkit.server;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.server.IBukkitServerManager;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class ScheduleShutdownCommand extends NorthCommand
{
    @Inject
    private IBukkitServerManager serverManager;

    public ScheduleShutdownCommand()
    {
        super("scheduleshutdown");
        this.setPermission("dev");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final boolean scheduled = this.serverManager.isShutdownScheduled();
        if (args.length() == 1 && "switch".equals(args.asString(0)))
        {
            if (scheduled)
            {
                sender.sendMessage("&aAnulowano zaplanowane wylaczenie instancji serwera");
                this.serverManager.cancelShutdown();
            }
            else
            {
                sender.sendMessage("&aZaplanowano wylaczenie instancji serwera");
                this.serverManager.scheduleShutdown();
            }
        }
        else
        {
            sender.sendMessage("&eAktualnie wylaczenie instancji serwera {0}", scheduled ? "&cjest zaplanowane" : "&anie jest zaplanowane");
            sender.sendMessage("&eWpisz /scheduleshutdown switch, aby zmienic");
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
