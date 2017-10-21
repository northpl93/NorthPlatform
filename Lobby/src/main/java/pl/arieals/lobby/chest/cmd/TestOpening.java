package pl.arieals.lobby.chest.cmd;

import org.bukkit.entity.Player;

import pl.arieals.lobby.chest.opening.ChestOpeningController;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class TestOpening extends NorthCommand
{
    @Inject
    private ChestOpeningController openingController;

    public TestOpening()
    {
        super("testopening");
        this.setPermission("dev");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();

        this.openingController.openOpeningGui(player);
    }
}