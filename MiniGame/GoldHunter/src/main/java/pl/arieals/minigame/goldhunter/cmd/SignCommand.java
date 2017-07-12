package pl.arieals.minigame.goldhunter.cmd;

import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.gamehost.MiniGameApi;
import pl.arieals.minigame.goldhunter.GameTeam;
import pl.arieals.minigame.goldhunter.GoldHunterPlayer;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;

public class SignCommand extends NorthCommand
{
    public SignCommand()
    {
        super("sign", "zapisz");
    }
    
    @Override
    public void execute(NorthCommandSender sender, Arguments args, String label)
    {
        if ( !sender.isPlayer() )
        {
            return;
        }
        
        GoldHunterPlayer player = MiniGameApi.getPlayerData((Player) sender.unwrapped(), GoldHunterPlayer.class);
        if ( player == null )
        {
            return;
        }
        
        GameTeam team = null;
        if ( args.asInt(0) != null )
        {
            team = GameTeam.values()[args.asInt(0)];
        }
        
        player.getArena().signToTeam(player, team);
        // TODO: message
    }
}
