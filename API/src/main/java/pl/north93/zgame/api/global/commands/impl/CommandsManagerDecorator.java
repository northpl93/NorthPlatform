package pl.north93.zgame.api.global.commands.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.BukkitCommandsManager;
import pl.north93.zgame.api.bungee.BungeeCommandsManager;
import pl.north93.zgame.api.global.commands.ICommandsManager;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Aggregator;
import pl.north93.zgame.api.standalone.commands.StandaloneCommandsManager;

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
                this.commandsManager = new BukkitCommandsManager();
                break;
            case BUNGEE:
                this.commandsManager = new BungeeCommandsManager();
                break;
            case STANDALONE:
                this.commandsManager = new StandaloneCommandsManager();
        }

        for (final NorthCommand northCommand : this.northCommands)
        {
            this.registerCommand(northCommand);
        }

        //this.getExtensionPoint(NorthCommand.class).setHandler(this::registerCommand);

        //final IAnnotatedExtensionPoint quickCommandExtension = (IAnnotatedExtensionPoint) this.getExtensionPoint(QuickCommand.class);
        //quickCommandExtension.setAnnotatedHandler(this::handleQuickCommandAnnotation);
    }

    @Aggregator(NorthCommand.class)
    public void handleNorthCommand(final NorthCommand northCommand)
    {
        if (this.commandsManager != null)
        {
            this.commandsManager.registerCommand(northCommand);
        }
        else
        {
            this.northCommands.add(northCommand);
        }
    }

    /*private void handleQuickCommandAnnotation(final IAnnotated annotated)
    {
        if (! annotated.isMethod())
        {
            return;
        }
        final QuickCommand annotation = annotated.getAnnotation();
        final Method method = (Method) annotated.getElement();

        this.registerCommand(new QuickNorthCommand(method, annotation));
    }*/

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
