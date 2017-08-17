package pl.arieals.lobby.cmd;

import org.bukkit.entity.Player;

import pl.arieals.lobby.gui.bedwars.BwShopMain;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;

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
