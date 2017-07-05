package pl.arieals.api.minigame.server.gamehost.cmd;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;


import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;

public class StartDeathMatch extends NorthCommand
{
    public StartDeathMatch()
    {
        super("startdeathmatch");
        this.setPermission("dev");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();
        final LocalArena arena = getArena(player);

        arena.getDeathMatch().activateDeathMatch();
        player.sendMessage(ChatColor.GREEN + "Done");
    }
}
