package pl.north93.zgame.api.bukkitcommands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;

public class FlyCmd extends NorthCommand
{
    public FlyCmd()
    {
        super("fly");
        this.setPermission("api.command.fly");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player;
        if (args.length() == 0)
        {
            player = (Player) sender.unwrapped();
        }
        else
        {
            player = Bukkit.getPlayer(args.asString(0));
            if (player == null)
            {
                sender.sendRawMessage("&cNie ma takiego gracza!");
                return;
            }
        }

        final boolean flying = player.getAllowFlight();
        if (flying)
        {
            player.setAllowFlight(false);
            sender.sendRawMessage("&cLatanie wylaczone dla " + player.getName());
        }
        else
        {
            player.setAllowFlight(true);
            sender.sendRawMessage("&cLatanie wlaczone dla " + player.getName());
        }
    }
}
