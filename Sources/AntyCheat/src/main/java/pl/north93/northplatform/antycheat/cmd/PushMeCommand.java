package pl.north93.northplatform.antycheat.cmd;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;

public class PushMeCommand extends NorthCommand
{
    public PushMeCommand()
    {
        super("pushme");
        this.setPermission("dev");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();
        player.setVelocity(new Vector(args.asDouble(0), args.asDouble(1), args.asDouble(2)));
    }
}
