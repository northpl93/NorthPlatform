package pl.north93.northplatform.features.bukkit.general;

import org.bukkit.entity.Player;

import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;

public class EnderchestCommand extends NorthCommand
{
    public EnderchestCommand()
    {
        super("enderchest", "echest");
        this.setPermission("basefeatures.cmd.enderchest");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player unwrapped = (Player) sender.unwrapped();
        unwrapped.openInventory(unwrapped.getEnderChest());
    }
}
