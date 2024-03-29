package pl.north93.northplatform.features.bukkit.general;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;

/**
 * Created by Konrad on 2017-02-12.
 */
public class LightningCommand extends NorthCommand
{
    public LightningCommand()
    {
        super("lightning", "thor");
        this.setPermission("basefeatures.cmd.lightning");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player unwrapped = (Player) sender.unwrapped();
        unwrapped.getWorld().strikeLightning(unwrapped.getTargetBlock((Set<Material>)null, 60).getLocation());
    }
}

