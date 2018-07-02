package pl.arieals.minigame.goldhunter.cmd;

import org.bukkit.entity.Player;

import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.player.GameTeam;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;

public class SignCommand extends NorthCommand
{
    private final GoldHunter goldHunter;
    
    public SignCommand(GoldHunter goldHunter)
    {
        super("sign", "zapisz", "dolacz", "join");
        
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
        
        GameTeam team = null;
        if ( args.has(0) )
        {
            team = GameTeam.valueOf(args.asString(0).toUpperCase());
        }
        
        player.getArena().trySignToTeam(player, team);
    }
}
