package pl.north93.zgame.features.bukkit.server;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import pl.north93.zgame.api.bukkit.world.IWorldManager;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class WorldUnloadCommand extends NorthCommand
{
	private final MessagesBox messages;
	private final IWorldManager worldManager;
	
	public WorldUnloadCommand(@Messages("Commands") MessagesBox messages, IWorldManager worldManager)
	{
		super("worldunload");
		setPermission("api.command.worldunload");
		
		this.messages = messages;
		this.worldManager = worldManager;
	}
	
	@Override
	public void execute(NorthCommandSender sender, Arguments args, String label)
	{	
		String worldToUnload = null;

		if ( args.length() > 1 )
		{
			sender.sendMessage(messages, "command.usage", "worldunload", "[world]");
			return;
		}
		
		if ( args.length() == 1 )
		{
			worldToUnload = args.asString(0);
		}
		
		if ( worldToUnload == null )
		{
			if ( sender.isPlayer() )
			{
				worldToUnload = ((Player) sender.unwrapped()).getWorld().getName();
			}
			else
			{
				sender.sendMessage(messages, "command.usage", "worldunload", "[world]");
				return;
			}
		}
		
		World world = Bukkit.getWorld(worldToUnload);
		
		if ( world == null )
		{
			sender.sendMessage(messages, "command.worldunload.not_loaded", worldToUnload);
			return;
		}
		
		worldManager.unloadWorld(world);
		sender.sendMessage(messages, "command.worldunload.unloaded");
	}
}
