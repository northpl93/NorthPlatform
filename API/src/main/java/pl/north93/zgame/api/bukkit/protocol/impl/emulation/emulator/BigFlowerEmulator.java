package pl.north93.zgame.api.bukkit.protocol.impl.emulation.emulator;

import java.util.TreeMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import pl.north93.zgame.api.bukkit.protocol.impl.emulation.BlockData;
import pl.north93.zgame.api.bukkit.protocol.impl.emulation.BlockEmulator;

public class BigFlowerEmulator implements BlockEmulator
{
    @Override
    public Material getType()
    {
        return Material.DOUBLE_PLANT;
    }

    @Override
    public boolean isApplicable(final Block block)
    {
        if (block.getType() != Material.DOUBLE_PLANT)
        {
            return false;
        }

        final Block upperFlower = block.getRelative(BlockFace.UP);
        if (upperFlower.getType() != Material.DOUBLE_PLANT)
        {
            return false; // there is no flower on the top
        }

        return block.getData() < 15; // lower part
    }

    @Override
    public BlockData getData(final Block block)
    {
        final Block upperFlower = block.getRelative(BlockFace.UP);

        final TreeMap<String, String> params = new TreeMap<>();
        params.put("half", "upper");
        switch (block.getData())
        {
            case 0:
                return new BlockData(upperFlower, "minecraft:sunflower", params);
            case 1:
                return new BlockData(upperFlower, "minecraft:lilac", params);
            case 2:
                return new BlockData(upperFlower, "minecraft:tall_grass", params);
            case 3:
                return new BlockData(upperFlower, "minecraft:large_fern", params);
            case 4:
                return new BlockData(upperFlower, "minecraft:rose_bush", params);
            case 5:
                return new BlockData(upperFlower, "minecraft:peony", params);
        }

        // default fallback, upper part of lilac
        return new BlockData(upperFlower, "minecraft:lilac", params);
    }
}
