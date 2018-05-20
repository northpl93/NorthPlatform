package pl.north93.zgame.features.bukkit.general;

import org.bukkit.entity.Player;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;

public class CraftingCommand extends NorthCommand
{
    public CraftingCommand()
    {
        super("crafting", "craft");
        this.setPermission("api.command.crafting");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player unwrapped = (Player) sender.unwrapped();
        unwrapped.openWorkbench(null, true);
    }
}
