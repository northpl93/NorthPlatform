package pl.north93.zgame.skyblock.server.cmd;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.tuple.Pair;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.skyblock.server.world.IslandLocation;

public class IsTestCmd extends NorthCommand
{
    public IsTestCmd()
    {
        super("test");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final int radius = args.asInt(0);
        final Player player = (Player) sender.unwrapped();
        sender.sendMessage("&cYour location: " + player.getLocation());
        sender.sendMessage("&cCenter chunk: " + player.getLocation().getChunk());
        sender.sendMessage("&cRadius: " + radius);

        final Chunk chunk = player.getLocation().getChunk();
        final IslandLocation islandLocation = new IslandLocation(player.getWorld(), chunk.getX(), chunk.getZ(), radius);

        final Pair<Location, Location> corners = islandLocation.getIslandCorners();
        sender.sendMessage("&cIsland upper left corner: x:" + corners.getLeft().getX() + " / z:" + corners.getLeft().getZ());
        sender.sendMessage("&cIsland lower right corner: x:" + corners.getRight().getX() + " / z:" + corners.getRight().getZ());
        sender.sendMessage("&cIsland chunks:" + islandLocation.getIslandChunks());

        final Location first = corners.getLeft();
        first.setY(5);
        final Location right = corners.getRight();
        right.setY(5);

        IslandLocation.blocksFromTwoPoints(first, right).forEach(block -> block.setType(Material.WOOL));
    }
}
