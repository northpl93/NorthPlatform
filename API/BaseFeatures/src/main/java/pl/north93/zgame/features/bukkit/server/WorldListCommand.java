package pl.north93.zgame.features.bukkit.server;

import org.bukkit.Bukkit;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class WorldListCommand extends NorthCommand
{
	private final MessagesBox messages;
	
	public WorldListCommand(@Messages("Commands") MessagesBox messages)
	{
		super("worldlist");
		setPermission("api.command.worldlist");
		
		this.messages = messages;
	}
	
	@Override
	public void execute(NorthCommandSender sender, Arguments args, String label)
	{
		sender.sendMessage(messages, "command.worldlist.header");
		Bukkit.getWorlds().forEach(w -> sender.sendMessage(messages, "command.worldlist.entry", w.getName()));
	}
}
