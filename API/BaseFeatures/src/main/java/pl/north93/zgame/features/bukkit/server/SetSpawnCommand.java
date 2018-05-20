package pl.north93.zgame.features.bukkit.server;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;

public class SetSpawnCommand extends NorthCommand
{
    public SetSpawnCommand()
    {
        super("setspawn");
        this.setPermission("api.command.setspawn");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();
        final World world = player.getWorld();
        final Location location = player.getLocation();
        sender.sendMessage("&aSpawn swiata " + world.getName() + " ustawiony!");
        world.setSpawnLocation((int)location.getX(), (int)location.getY(), (int)location.getZ());
    }
}
