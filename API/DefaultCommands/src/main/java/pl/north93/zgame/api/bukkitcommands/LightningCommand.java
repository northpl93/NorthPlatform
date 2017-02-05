package pl.north93.zgame.api.bukkitcommands;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;

import java.util.Set;

/**
 * Created by Konrad on 2017-02-12.
 */
public class LightningCommand extends NorthCommand
{
    public LightningCommand()
    {
        super("lightning", "thor");
        this.setPermission("api.command.lightning");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player unwrapped = (Player) sender.unwrapped();
        unwrapped.getWorld().strikeLightning(unwrapped.getTargetBlock((Set<Material>)null, 60).getLocation());
    }
}

