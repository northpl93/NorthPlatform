package pl.mcpiraci.world.properties.impl.cmd;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.mcpiraci.world.properties.WorldProperties;
import pl.mcpiraci.world.properties.WorldPropertiesManager;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class ReloadCommand extends NorthCommand
{
    private final WorldPropertiesManager propertiesManager;
    private final MessagesBox messages;
    
    public ReloadCommand(WorldPropertiesManager propertiesManager, @Messages("commands") MessagesBox messages)
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
            Bukkit.getWorlds().stream().map(propertiesManager::getProperties).forEach(WorldProperties::reloadWorldConfig);
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
        
        WorldProperties properties = propertiesManager.getProperties(worldName);
        if ( properties == null )
        {
            sender.sendMessage(messages, "reload.invalid_world");
            return;
        }
        
        properties.reloadWorldConfig();
        sender.sendMessage(messages, "reload.success");
    }
}
