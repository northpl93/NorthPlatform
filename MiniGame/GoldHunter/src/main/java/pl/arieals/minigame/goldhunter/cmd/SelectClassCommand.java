package pl.arieals.minigame.goldhunter.cmd;

import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.gamehost.MiniGameApi;
import pl.arieals.minigame.goldhunter.GoldHunterPlayer;
import pl.arieals.minigame.goldhunter.classes.CharacterClassManager;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;

public class SelectClassCommand extends NorthCommand
{
    private final CharacterClassManager classManager;
    
    //@Bean
    public SelectClassCommand(CharacterClassManager classManager)
    {
        super("ghdebug-selectclass", "ghd-sc");
        this.classManager = classManager;
    }
    
    @Override
    public void execute(NorthCommandSender sender, Arguments args, String label)
    {
        if  ( !sender.isPlayer() ) // TODO check permission
        {
            return; // TODO: send command not found message
        }
        
        GoldHunterPlayer player = MiniGameApi.getPlayerData((Player) sender.unwrapped(), GoldHunterPlayer.class);
        player.selectClass(classManager.getByName(args.asString(0)));
    }
    
}
