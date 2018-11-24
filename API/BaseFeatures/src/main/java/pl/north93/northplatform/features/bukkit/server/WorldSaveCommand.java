package pl.north93.northplatform.features.bukkit.server;

import org.bukkit.Bukkit;
import org.bukkit.World;

import pl.north93.northplatform.api.bukkit.world.IWorldManager;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;

public class WorldSaveCommand extends NorthCommand
{
    private final MessagesBox messages;
    private final IWorldManager worldManager;

    public WorldSaveCommand(@Messages("BaseFeatures") MessagesBox messages, IWorldManager worldManager)
    {
        super("worldsave");
        setPermission("basefeatures.cmd.worldsave");

        this.messages = messages;
        this.worldManager = worldManager;
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() != 1)
        {
            sender.sendMessage(messages, "command.usage", "worldsave", "<nazwa>");
            return;
        }

        final World world = Bukkit.getWorld(args.asString(0));
        if (world == null)
        {
            sender.sendMessage(messages, "command.worldsave.no_world");
            return;
        }

        this.worldManager.save(world);
        sender.sendMessage(messages, "command.worldsave.saved");
    }
}
