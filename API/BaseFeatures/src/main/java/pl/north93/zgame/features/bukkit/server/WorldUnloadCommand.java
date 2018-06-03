package pl.north93.zgame.features.bukkit.server;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class WorldUnloadCommand extends NorthCommand
{
	private final MessagesBox messages;
	
	public WorldUnloadCommand(@Messages("Commands") MessagesBox messages)
	{
		super("worldunload");
		setPermission("api.command.worldunload");
		
		this.messages = messages;
	}
	
	@Override
	public void execute(NorthCommandSender sender, Arguments args, String label)
	{	
		String worldToUnload = null;
		boolean force = false;
		
		if ( args.length() == 1 && args.asString(0).equalsIgnoreCase("--force"))
		{
			force = true;
		}
		else if ( args.length() == 1 )
		{
			worldToUnload = args.asString(0);
		}
		else if ( args.length() == 2 && args.asString(0).equalsIgnoreCase("--force") )
		{
			worldToUnload = args.asString(1);
			force = true;
		}
		else if ( args.length() == 2 && args.asString(1).equalsIgnoreCase("--force") )
		{
			worldToUnload = args.asString(0);
			force = true;
		}
		else if ( args.length() != 0 )
		{
			sender.sendMessage(messages, "command.usage", "worldunload", "[world] [--force]");
			return;
		}
		
		if ( worldToUnload == null )
		{
			if ( sender.isPlayer() )
			{
				worldToUnload = ((Player) sender.unwrapped()).getWorld().getName();
			}
			else
			{
				sender.sendMessage(messages, "command.usage", "worldunload", "[world] [--force]");
				return;
			}
		}
		
		World world = Bukkit.getWorld(worldToUnload);
		
		if ( world == null )
		{
			sender.sendMessage(messages, "command.worldunload.not_loaded", worldToUnload);
			return;
		}
		
		if ( force )
		{
			World main = Bukkit.getWorlds().get(0);
			world.getPlayers().forEach(p -> p.teleport(main.getSpawnLocation()));
		}
		
		if ( Bukkit.unloadWorld(world, false) )
		{
			sender.sendMessage(messages, "command.worldunload.unloaded");
		}
		else
		{
			sender.sendMessage(messages, "command.worldunload.cannot_unload");
		}
	}
}
