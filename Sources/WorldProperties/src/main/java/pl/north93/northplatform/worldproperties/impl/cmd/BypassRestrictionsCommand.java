package pl.north93.northplatform.worldproperties.impl.cmd;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.north93.northplatform.worldproperties.IPlayerProperties;
import pl.north93.northplatform.worldproperties.IWorldPropertiesManager;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;

public class BypassRestrictionsCommand extends NorthCommand
{
    private final IWorldPropertiesManager propertiesManager;
    private final MessagesBox             messages;
    
    public BypassRestrictionsCommand(IWorldPropertiesManager propertiesManager, @Messages("commands") MessagesBox messages)
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
        
        IPlayerProperties playerProperties = propertiesManager.getPlayerProperties(player);
        playerProperties.setCanBypassRestriction(!playerProperties.canBypassRestrictions());
        
        sender.sendMessage(messages, playerProperties.canBypassRestrictions() ? "br.on" : "br.off", player.getName());
    }
}