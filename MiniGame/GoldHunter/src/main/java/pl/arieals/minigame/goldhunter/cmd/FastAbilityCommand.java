package pl.arieals.minigame.goldhunter.cmd;

import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;

import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;

public class FastAbilityCommand extends NorthCommand
{
    private final GoldHunter goldHunter;
    
    public FastAbilityCommand(final GoldHunter goldHunter)
    {
        super("ghdebug-fastability", "ghd-fa");
        setPermission("goldhunter.debug");
        
        this.goldHunter = goldHunter;
    }
    
    @Override
    public void execute(NorthCommandSender sender, Arguments args, String label)
    {
        Preconditions.checkState(sender.isPlayer());
        
        GoldHunterPlayer player = goldHunter.getPlayer((Player) sender.unwrapped());
        
        if ( player.getAbilityTracker().isFastAbilityEnabled() )
        {
            player.getAbilityTracker().setFastAbilityEnabled(false);
            player.getPlayer().sendMessage("GHD: Fast ability loading disabled!");
        }
        else
        {
            player.getAbilityTracker().setFastAbilityEnabled(true);
            player.getPlayer().sendMessage("GHD: Fast ability loading enabled");
        }
    }
    
}
