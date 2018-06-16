package pl.north93.zgame.features.bukkit.server;

import java.util.HashSet;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

import pl.north93.zgame.api.bukkit.utils.region.Cuboid;
import pl.north93.zgame.api.bukkit.utils.xml.XmlChunk;
import pl.north93.zgame.api.bukkit.world.IWorldManager;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class WorldTrim extends NorthCommand
{
	private final MessagesBox messages;
	private final IWorldManager worldManager;
	
	private WorldTrim(IWorldManager worldManager, @Messages("BaseFeatures") MessagesBox messages)
	{
		super("worldtrim");
		setPermission("api.command.worldtrim");
		
		this.messages = messages;
		this.worldManager = worldManager;
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
			sender.sendMessage(messages, "command.usage", "<target>");
			return;
		}
		
		final WorldEditPlugin worldEditPlugin = JavaPlugin.getPlugin(WorldEditPlugin.class);
        final Selection selection = worldEditPlugin.getSelection((Player) sender.unwrapped());
        
        if ( selection == null )
        {
        	sender.sendMessage(messages, "worldtrim.select_area_first");
        	return;
        }
        
        World world = selection.getWorld();
        Cuboid cuboid = new Cuboid(selection.getMinimumPoint(), selection.getMaximumPoint());
        
        HashSet<XmlChunk> chunks = cuboid.getChunksCoordinates().stream().map(xz -> new XmlChunk(xz.getKey(), xz.getValue()))
        		.collect(Collectors.toCollection(HashSet::new));
        
        sender.sendMessage(messages, "worldtrim.start_trimming");
        worldManager.trimWorld(world, args.asString(0), chunks);
        sender.sendMessage(messages, "worldtrim.success");
	}
}
