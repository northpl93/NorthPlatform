package pl.north93.northplatform.features.bukkit.general;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;

public class FlyCommand extends NorthCommand
{
    public FlyCommand()
    {
        super("fly");
        this.setPermission("basefeatures.cmd.fly");
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
                sender.sendMessage("&cNie ma takiego gracza!");
                return;
            }
        }

        final boolean flying = player.getAllowFlight();
        if (flying)
        {
            player.setAllowFlight(false);
            sender.sendMessage("&cLatanie wylaczone dla " + player.getName());
        }
        else
        {
            player.setAllowFlight(true);
            sender.sendMessage("&cLatanie wlaczone dla " + player.getName());
        }
    }
}
