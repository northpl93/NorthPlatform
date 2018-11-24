package pl.north93.northplatform.api.bukkit.world.impl;

import javax.annotation.Nullable;

import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.Blocks;
import net.minecraft.server.v1_12_R1.Chunk;
import net.minecraft.server.v1_12_R1.IBlockData;
import net.minecraft.server.v1_12_R1.World;

/*default*/ class DummyChunk extends Chunk
{
    public DummyChunk(final World world, final int i, final int j)
    {
        super(world, i, j);
    }

    @Override
    public IBlockData a(final int i, final int j, final int k)
    {
        return Blocks.AIR.getBlockData();
    }

    @Nullable
    @Override
    public IBlockData a(final BlockPosition blockposition, final IBlockData iblockdata)
    {
        return null;
    }
}

