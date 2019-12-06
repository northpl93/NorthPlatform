package pl.arieals.minigame.goldhunter.cmd;

import org.bukkit.entity.Player;

import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.classes.CharacterClassManager;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;

public class SelectClassCommand extends NorthCommand
{
    private final GoldHunter goldHunter;
    private final CharacterClassManager classManager;
    
    public SelectClassCommand(GoldHunter goldHunter, CharacterClassManager classManager)
    {
        super("ghdebug-selectclass", "ghd-sc");
        setPermission("goldhunter.debug");
        
        this.goldHunter = goldHunter;
        this.classManager = classManager;
    }
    
    @Override
    public void execute(NorthCommandSender sender, Arguments args, String label)
    {
        if  ( !sender.isPlayer() ) // TODO check permission
        {
            return; // TODO: send command not found message
        }
        
        GoldHunterPlayer player = goldHunter.getPlayer((Player) sender.unwrapped());
        player.selectClass(classManager.getByName(args.asString(0)));
    }
    
}
