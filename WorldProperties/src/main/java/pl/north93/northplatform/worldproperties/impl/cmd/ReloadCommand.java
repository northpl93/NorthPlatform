package pl.north93.northplatform.worldproperties.impl.cmd;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.north93.northplatform.worldproperties.IWorldProperties;
import pl.north93.northplatform.worldproperties.IWorldPropertiesManager;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;

public class ReloadCommand extends NorthCommand
{
    private final IWorldPropertiesManager propertiesManager;
    private final MessagesBox             messages;
    
    public ReloadCommand(IWorldPropertiesManager propertiesManager, @Messages("commands") MessagesBox messages)
    {
        super("wpreload", "wpreloadall", "wpreloadserver");
        setPermission("worldproperties.cmd.wpreload");
        
        this.propertiesManager = propertiesManager;
        this.messages = messages;
    }
    
    @Override
    public void execute(NorthCommandSender sender, Arguments args, String label)
    {
        if ( label.equalsIgnoreCase("wpreloadall") )
        {
            Bukkit.getWorlds().stream().map(propertiesManager::getProperties).forEach(IWorldProperties::reloadWorldConfig);
            sender.sendMessage(messages, "reload.all.success");
            return;
        }
        
        if ( label.equalsIgnoreCase("wpreloadserver") )
        {
            propertiesManager.reloadServerConfig();
            sender.sendMessage(messages, "reload.server.success");
            return;
        }
        
        if ( args.length() > 1 )
        {
            sender.sendMessage(messages, "reload.usage");
            return;
        }
        
        String worldName;
        if ( args.length() == 0 )
        {
            if ( !sender.isPlayer() )
            {
                sender.sendMessage(messages, "reload.usage");
                return;
            }
            
            worldName = ((Player) sender.unwrapped()).getWorld().getName();
        }
        else
        {
            worldName = args.asString(0);
        }
        
        IWorldProperties properties = propertiesManager.getProperties(worldName);
        if ( properties == null )
        {
            sender.sendMessage(messages, "reload.invalid_world");
            return;
        }
        
        properties.reloadWorldConfig();
        sender.sendMessage(messages, "reload.success");
    }
}
