package pl.north93.northplatform.lobby.gui.bedwars;

import org.bukkit.entity.Player;

import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;

public class BwOutgameShop extends NorthCommand
{
    public BwOutgameShop()
    {
        super("bwoutgame");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();

        final BwShopMain bwShopMain = new BwShopMain(player);
        bwShopMain.open(player);
    }
}
