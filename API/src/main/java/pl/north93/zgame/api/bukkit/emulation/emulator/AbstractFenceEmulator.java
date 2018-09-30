package pl.north93.zgame.api.bukkit.emulation.emulator;

import java.util.TreeMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import lombok.ToString;
import pl.north93.zgame.api.bukkit.emulation.BlockData;
import pl.north93.zgame.api.bukkit.emulation.BlockEmulator;

@ToString
public abstract class AbstractFenceEmulator implements BlockEmulator
{
    private final Material fenceMaterial;
    private final String   newFenceId;

    public AbstractFenceEmulator(final Material fenceMaterial, final String newFenceId)
    {
        this.fenceMaterial = fenceMaterial;
        this.newFenceId = newFenceId;
    }

    @Override
    public Material getType()
    {
        return this.fenceMaterial;
    }

    @Override
    public boolean isApplicable(final Block block)
    {
        return block.getType() == this.fenceMaterial;
    }

    @Override
    public BlockData getData(final Block block)
    {
        final TreeMap<String, String> properties = new TreeMap<>();
        properties.put("waterlogged", "false");

        properties.put("north", checkFace(block, BlockFace.NORTH));
        properties.put("east", checkFace(block, BlockFace.EAST));
        properties.put("south", checkFace(block, BlockFace.SOUTH));
        properties.put("west", checkFace(block, BlockFace.WEST));

        return new BlockData(block, this.newFenceId, properties);
    }

    public static String checkFace(final Block block, final BlockFace face)
    {
        final Block relative = block.getRelative(face);
        return String.valueOf(relative.getType().isSolid());
    }
}
