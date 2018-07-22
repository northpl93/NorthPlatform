package pl.north93.zgame.api.bukkit.protocol.impl.emulation.emulator;

import java.util.TreeMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import pl.north93.zgame.api.bukkit.protocol.impl.emulation.BlockData;
import pl.north93.zgame.api.bukkit.protocol.impl.emulation.BlockEmulator;

public class CobblestoneWallEmulator implements BlockEmulator
{
    @Override
    public Material getType()
    {
        return Material.COBBLE_WALL;
    }

    @Override
    public boolean isApplicable(final Block block)
    {
        return block.getType() == Material.COBBLE_WALL;
    }

    @Override
    public BlockData getData(final Block block)
    {
        final TreeMap<String, String> properties = new TreeMap<>();
        properties.put("waterlogged", "false");

        // skopiowane z MCP
        final boolean connectionNorth = block.getRelative(BlockFace.NORTH).getType().isSolid();
        final boolean connectionEast = block.getRelative(BlockFace.EAST).getType().isSolid();
        final boolean connectionSouth = block.getRelative(BlockFace.SOUTH).getType().isSolid();
        final boolean connectionWest = block.getRelative(BlockFace.WEST).getType().isSolid();
        final boolean flag4 = connectionNorth && !connectionEast && connectionSouth && !connectionWest || !connectionNorth && connectionEast && !connectionSouth && connectionWest;

        properties.put("up", String.valueOf(! flag4 || ! (block.getRelative(BlockFace.UP).getType() == Material.AIR)));
        properties.put("north", String.valueOf(connectionNorth));
        properties.put("east", String.valueOf(connectionEast));
        properties.put("south", String.valueOf(connectionSouth));
        properties.put("west", String.valueOf(connectionWest));

        final String id = block.getData() == 0 ? "minecraft:cobblestone_wall" : "minecraft:mossy_cobblestone_wall";
        return new BlockData(block, id, properties);
    }
}
