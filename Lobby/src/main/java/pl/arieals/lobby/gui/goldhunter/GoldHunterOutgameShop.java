package pl.arieals.lobby.gui.goldhunter;

import org.bukkit.entity.Player;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;

public class GoldHunterOutgameShop extends NorthCommand
{
	public GoldHunterOutgameShop()
	{
		super("ghoutgame");
	}

	@Override
	public void execute(NorthCommandSender sender, Arguments args, String label)
	{
		Player player = (Player) sender.unwrapped();
		GoldHunterShopGui.openMainGui(player);
	}
	
}
