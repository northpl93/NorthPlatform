package pl.north93.northplatform.lobby.gui.goldhunter;

import org.bukkit.entity.Player;

import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;

public class GoldHunterOutgameShop extends NorthCommand
{
	public GoldHunterOutgameShop()
	{
		super("ghoutgame");
		setPermission("dev");
	}

	@Override
	public void execute(NorthCommandSender sender, Arguments args, String label)
	{
		Player player = (Player) sender.unwrapped();
		GoldHunterShopGui.openMainGui(player);
	}
	
}
