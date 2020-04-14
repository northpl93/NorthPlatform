package pl.north93.northplatform.api.global.commands.impl;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.BukkitApiCore;
import pl.north93.northplatform.api.bungee.BungeeApiCore;
import pl.north93.northplatform.api.global.commands.annotation.QuickCommand;
import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.component.IBeanContext;
import pl.north93.northplatform.api.bukkit.BukkitCommandsManager;
import pl.north93.northplatform.api.bungee.BungeeCommandsManager;
import pl.north93.northplatform.api.global.commands.ICommandsManager;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.component.annotations.bean.Aggregator;
import pl.north93.northplatform.api.standalone.commands.StandaloneCommandsManager;

public class CommandsManagerDecorator extends Component implements ICommandsManager
{
    private Set<NorthCommand> northCommands = new HashSet<>();
    private ICommandsManager commandsManager;

    @Override
    protected void enableComponent()
    {
        switch (this.getApiCore().getPlatform())
        {
            case BUKKIT:
                this.commandsManager = new BukkitCommandsManager((BukkitApiCore) this.getApiCore());
                break;
            case BUNGEE:
                this.commandsManager = new BungeeCommandsManager((BungeeApiCore) this.getApiCore());
                break;
            case STANDALONE:
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
