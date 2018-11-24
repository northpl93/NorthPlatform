package pl.north93.northplatform.features.bukkit.server;

import org.bukkit.Bukkit;

import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;

public class WorldListCommand extends NorthCommand
{
	private final MessagesBox messages;
	
	public WorldListCommand(@Messages("BaseFeatures") MessagesBox messages)
	{
		super("worldlist");
		setPermission("basefeatures.cmd.worldlist");
		
		this.messages = messages;
	}
	
	@Override
	public void execute(NorthCommandSender sender, Arguments args, String label)
	{
		sender.sendMessage(messages, "command.worldlist.header");
		Bukkit.getWorlds().forEach(w -> sender.sendMessage(messages, "command.worldlist.entry", w.getName()));
	}
}
