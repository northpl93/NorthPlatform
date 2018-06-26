package pl.arieals.minigame.goldhunter.cmd;

import org.bukkit.entity.Player;

import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;

public class UnsignCommand extends NorthCommand
{
    private final GoldHunter goldHunter;
    
    public UnsignCommand(GoldHunter goldHunter)
    {
        super("unsign", "leave", "oposc");
        
        this.goldHunter = goldHunter;
    }
    
    @Override
    public void execute(NorthCommandSender sender, Arguments args, String label)
    {
        if ( !sender.isPlayer() )
        {
            return;
        }
        
        GoldHunterPlayer player = goldHunter.getPlayer((Player) sender.unwrapped());
        if ( player == null )
        {
            return;
        }
        
        player.getArena().unsignFromTeam(player);
    }
}
