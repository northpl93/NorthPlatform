package pl.mcpiraci.world.properties.impl.cmd;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.mcpiraci.world.properties.PlayerProperties;
import pl.mcpiraci.world.properties.WorldPropertiesManager;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class BypassRestrictionsCommand extends NorthCommand
{
    private final WorldPropertiesManager propertiesManager;
    private final MessagesBox messages;
    
    public BypassRestrictionsCommand(WorldPropertiesManager propertiesManager, @Messages("commands") MessagesBox messages)
    {
        super("br", "bypass-restrictions");
        setPermission("worldproperties.cmd.br");
        
        this.propertiesManager = propertiesManager;
        this.messages = messages;
    }
    
    @Override
    public void execute(NorthCommandSender sender, Arguments args, String label)
    {
        if ( args.length() > 1 || ( args.length() == 0 && !sender.isPlayer() ) )
        {
            sender.sendMessage(messages, "br.usage");
            return;
        }
        
        Player player;
        if ( args.length() == 0 )
        {
            player = (Player) sender.unwrapped();
        }
        else
        {
            player = Bukkit.getPlayer(args.asString(0));
        }
        
        PlayerProperties playerProperties = propertiesManager.getPlayerProperties(player);
        playerProperties.setCanBypassRestriction(!playerProperties.canBypassRestrictions());
        
        sender.sendMessage(messages, playerProperties.canBypassRestrictions() ? "br.on" : "br.off", player.getName());
    }
}