package pl.north93.zgame.features.bukkit.server;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class WorldTeleportCommand extends NorthCommand
{
	private final MessagesBox messages;
	
	public WorldTeleportCommand(@Messages("BaseFeatures") MessagesBox messages)
	{
		super("worldteleport", "wtp");
		setPermission("api.command.worldlist");
		
		this.messages = messages;
	}
	
	@Override
	public void execute(NorthCommandSender sender, Arguments args, String label)
	{
		if ( !sender.isPlayer() )
		{
			sender.sendMessage(messages, "command.only_players");
			return;
		}
		
		if ( args.length() != 1 )
		{
			sender.sendMessage(messages, "command.usage", "[world]");
			return;
		}
		
		World worldToTeleport = Bukkit.getWorld(args.asString(0));
		if ( worldToTeleport == null )
		{
			sender.sendMessage(messages, "command.wtp.not_loaded");
			return;
		}
		
		((Player) sender.unwrapped()).teleport(worldToTeleport.getSpawnLocation());
	}
}
