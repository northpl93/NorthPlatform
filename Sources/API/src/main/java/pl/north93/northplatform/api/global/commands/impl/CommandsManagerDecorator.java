package pl.north93.northplatform.api.global.commands.impl;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.BukkitCommandsManager;
import pl.north93.northplatform.api.bukkit.BukkitHostConnector;
import pl.north93.northplatform.api.bungee.BungeeCommandsManager;
import pl.north93.northplatform.api.bungee.BungeeHostConnector;
import pl.north93.northplatform.api.global.HostConnector;
import pl.north93.northplatform.api.global.HostId;
import pl.north93.northplatform.api.global.commands.ICommandsManager;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.annotation.QuickCommand;
import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.component.IBeanContext;
import pl.north93.northplatform.api.global.component.annotations.bean.Aggregator;
import pl.north93.northplatform.api.standalone.commands.StandaloneCommandsManager;

public class CommandsManagerDecorator extends Component implements ICommandsManager
{
    private final Set<NorthCommand> northCommands = new HashSet<>();
    private ICommandsManager commandsManager;

    @Override
    protected void enableComponent()
    {
        final HostConnector hostConnector = this.getApiCore().getHostConnector();

        final String hostId = this.getApiCore().getHostId().toString();
        if ("bukkit".equals(hostId))
        {
            this.commandsManager = new BukkitCommandsManager((BukkitHostConnector) hostConnector);
        }
        else if ("bungee".equals(hostId))
        {
            this.commandsManager = new BungeeCommandsManager((BungeeHostConnector) hostConnector);
        }
        else
        {
            this.commandsManager = new StandaloneCommandsManager();
        }

        for (final NorthCommand northCommand : this.northCommands)
        {
            this.registerCommand(northCommand);
        }
    }

    @Aggregator(NorthCommand.class)
    public void handleNorthCommand(final NorthCommand northCommand)
    {
        if (this.commandsManager != null)
        {
            this.registerCommand(northCommand);
        }
        else
        {
            this.northCommands.add(northCommand);
        }
    }

    @Aggregator(QuickCommand.class)
    public void handleQuickCommand(final IBeanContext context, final QuickCommand annotation, final Method target)
    {
        final QuickNorthCommand command = new QuickNorthCommand(target, annotation);
        if (this.commandsManager != null)
        {
            this.registerCommand(command);
        }
        else
        {
            this.northCommands.add(command);
        }
    }

    @Override
    protected void disableComponent()
    {
    }

    @Override
    public void registerCommand(final NorthCommand northCommand)
    {
        this.commandsManager.registerCommand(northCommand);
    }

    @Override
    public void stop()
    {
        this.commandsManager.stop();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("commandsManager", this.commandsManager).toString();
    }
}
