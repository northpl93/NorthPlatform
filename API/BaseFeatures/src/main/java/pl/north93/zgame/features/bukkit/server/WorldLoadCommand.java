package pl.north93.zgame.features.bukkit.server;

import java.io.File;

import org.bukkit.Bukkit;

import pl.north93.zgame.api.bukkit.utils.ISyncCallback;
import pl.north93.zgame.api.bukkit.world.IWorldManager;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class WorldLoadCommand extends NorthCommand
{
	private final MessagesBox messages;
	private final IWorldManager worldManager;
	
	public WorldLoadCommand(@Messages("BaseFeatures") MessagesBox messages, IWorldManager worldManager)
	{
		super("worldload");
		setPermission("api.command.worldload");
		
		this.messages = messages;
		this.worldManager = worldManager;
	}
	
	@Override
	public void execute(NorthCommandSender sender, Arguments args, String label)
	{	
		String worldToLoad = null;
		boolean force = false;
		
		if ( args.length() == 1 )
		{
			worldToLoad = args.asString(0);
		}
		else if ( args.length() == 2 && args.asString(1).equalsIgnoreCase("--force") )
		{
			worldToLoad = args.asString(0);
			force = true;
		}
		else
		{
			sender.sendMessage(messages, "command.usage", "worldload", "<nazwa> [--force]");
			return;
		}
		
		File worldFolder = new File(Bukkit.getWorldContainer(), worldToLoad);
		
		if ( !worldFolder.isDirectory() && !force )
		{
			sender.sendMessage(messages, "command.worldload.cannot_load", worldToLoad);
			return;
		}
		
		sender.sendMessage(messages, "command.worldload.loading");
		ISyncCallback callback = worldManager.loadWorld(worldToLoad, null, false, false); // TODO: add way to config this
		callback.onComplete(() -> sender.sendMessage(messages, "command.worldload.loaded"));
	}
}
