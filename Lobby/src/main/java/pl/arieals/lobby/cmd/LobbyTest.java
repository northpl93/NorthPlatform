package pl.arieals.lobby.cmd;

import org.bukkit.entity.Player;

import pl.arieals.lobby.chest.opening.ChestOpeningController;
import pl.north93.zgame.api.bukkit.hologui.IHoloGuiManager;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class LobbyTest extends NorthCommand
{
    @Inject
    private IHoloGuiManager holoGuiManager;
    @Inject
    private ChestOpeningController openingController;

    public LobbyTest()
    {
        super("testopening");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();
        //this.holoGuiManager.openGui(player, new TestHoloGui());

        this.openingController.openOpeningGui(player);
    }
}