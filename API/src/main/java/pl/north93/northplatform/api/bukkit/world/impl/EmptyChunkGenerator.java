package pl.north93.northplatform.api.bukkit.world.impl;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;

/*default*/ class EmptyChunkGenerator extends ChunkGenerator
{
    private static EmptyChunkGenerator instance;
    
    private EmptyChunkGenerator()
    {
    }

    @Override
    public byte[][] generateBlockSections(World world, Random random, int x, int z, BiomeGrid biomes)
    {
        for ( int i = 0; i < 16; i++ )
        {
            for ( int j = 0; j < 16; j++ )
            {
                biomes.setBiome(i, j, Biome.PLAINS);
            }
        }
        
        return new byte[16][];
    }
    
    @Override
    public Location getFixedSpawnLocation(World world, Random random)
    {
        return new Location(world, 0, 64, 0);
    }
    
    public static EmptyChunkGenerator getInstance()
    {
        if ( instance == null )
        {
            instance = new EmptyChunkGenerator();
        }
        
        return instance;
    }
}
