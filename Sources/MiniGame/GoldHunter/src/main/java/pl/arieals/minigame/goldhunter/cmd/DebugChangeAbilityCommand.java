package pl.arieals.minigame.goldhunter.cmd;

import java.util.Arrays;

import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;

import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.classes.SpecialAbilityType;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;

public class DebugChangeAbilityCommand extends NorthCommand
{
    private final GoldHunter goldHunter;
    
    public DebugChangeAbilityCommand(final GoldHunter goldHunter)
    {
        super("ghdebug-changeability", "ghd-ca");
        this.setPermission("goldhunter.debug");
        
        this.goldHunter = goldHunter;
    }
    
    @Override
    public void execute(NorthCommandSender sender, Arguments args, String label)
    {
        Preconditions.checkState(sender.isPlayer());
        
        GoldHunterPlayer player = goldHunter.getPlayer((Player) sender.unwrapped());
        if ( player == null || !player.isIngame() )
        {
            player.getPlayer().sendMessage("&c&lGHD: &cMusisz byc w grze!");
            return;
        }
        
        if ( args.length() != 1 )
        {
            player.getPlayer().sendMessage("&c&lGHD: &cMozliwe wartosci: " + String.join("§c, §4", Arrays.stream(SpecialAbilityType.values()).map(SpecialAbilityType::name)
                    .toArray(i -> new String[i])));
        }
        
        SpecialAbilityType ability;
        try
        {
            ability = SpecialAbilityType.valueOf(args.asString(0).toUpperCase());
        }
        catch ( EnumConstantNotPresentException e )
        {
            player.getPlayer().sendMessage("§c§lGHD: §cNie ma takiej umiejetnosci");
            return;
        }
        
        
        player.getAbilityTracker().setNewAbilityType(ability);
        player.getPlayer().sendMessage("§c§lGHD: §aZmieniono umiejetnosc na " + ability.name());
    }
}
