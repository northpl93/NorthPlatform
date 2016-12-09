package pl.north93.zgame.api.global.commands.impl;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.BukkitCommandsManager;
import pl.north93.zgame.api.bungee.BungeeCommandsManager;
import pl.north93.zgame.api.global.commands.ICommandsManager;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.component.Component;

public class CommandsManagerDecorator extends Component implements ICommandsManager
{
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
            // TODO standalone
        }
        this.getExtensionPoint(NorthCommand.class).setHandler(this::registerCommand);
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
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("commandsManager", this.commandsManager).toString();
    }
}
