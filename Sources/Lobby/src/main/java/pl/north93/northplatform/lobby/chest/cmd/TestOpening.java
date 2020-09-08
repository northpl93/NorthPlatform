package pl.north93.northplatform.lobby.chest.cmd;

import pl.north93.northplatform.api.bukkit.player.IBukkitPlayers;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.lobby.chest.opening.ChestOpeningController;

public class TestOpening extends NorthCommand
{
    @Inject
    private IBukkitPlayers bukkitPlayers;
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
        final INorthPlayer player = this.bukkitPlayers.getPlayer(sender);

        this.openingController.openOpeningGui(player);
    }
}