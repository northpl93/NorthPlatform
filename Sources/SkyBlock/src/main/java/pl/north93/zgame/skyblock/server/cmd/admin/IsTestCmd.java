package pl.north93.zgame.skyblock.server.cmd.admin;

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
        this.setPermission("dev");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final int radius = args.asInt(0);
        final Player player = (Player) sender.unwrapped();
        sender.sendRawMessage("&cYour location: " + player.getLocation());
        sender.sendRawMessage("&cCenter chunk: " + player.getLocation().getChunk());
        sender.sendRawMessage("&cRadius: " + radius);

        final Chunk chunk = player.getLocation().getChunk();
        final IslandLocation islandLocation = new IslandLocation(player.getWorld(), chunk.getX(), chunk.getZ(), radius);

        final Pair<Location, Location> corners = islandLocation.getIslandCorners();
        sender.sendRawMessage("&cIsland upper left corner: x:" + corners.getLeft().getX() + " / z:" + corners.getLeft().getZ());
        sender.sendRawMessage("&cIsland lower right corner: x:" + corners.getRight().getX() + " / z:" + corners.getRight().getZ());
        sender.sendRawMessage("&cIsland chunks:" + islandLocation.getIslandChunks());
        sender.sendRawMessage("&cCalculated island chunks count: " + islandLocation.chunksCount());

        final Location first = corners.getLeft();
        first.setY(5);
        final Location right = corners.getRight();
        right.setY(5);

        IslandLocation.blocksFromTwoPoints(first, right).forEach(block -> block.setType(Material.WOOL, false));
    }
}
