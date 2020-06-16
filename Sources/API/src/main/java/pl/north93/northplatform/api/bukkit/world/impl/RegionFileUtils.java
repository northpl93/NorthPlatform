package pl.north93.northplatform.api.bukkit.world.impl;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.server.v1_12_R1.RegionFile;
import net.minecraft.server.v1_12_R1.RegionFileCache;

import org.bukkit.World;

import lombok.experimental.UtilityClass;
import pl.north93.northplatform.api.bukkit.world.ChunkLocation;

@UtilityClass
class RegionFileUtils
{    
    Set<ChunkLocation> getGeneratedChunks(World bukkitWorld)
    {
        return getGeneratedChunks(bukkitWorld.getWorldFolder());
    }
    
    Set<ChunkLocation> getGeneratedChunks(File worldFolder)
    {
        Set<ChunkLocation> result = new HashSet<>();
        
        for ( String file : listRegionFiles(worldFolder) )
        {
            String[] split = file.split("\\.");
            
            if ( split.length != 4 || !split[0].equals("r") || !split[3].equals("mca") )
            {
                // that's not the file we're looking for
                continue;
            }
            
            int x, z;
            try
            {
                x = Integer.parseInt(split[1]);
                z = Integer.parseInt(split[2]);
            }
            catch ( NumberFormatException e )
            {
                continue;
            }
            
            handleRegionFile(worldFolder, x, z, result);
        }
        
        return result;
    }
    
    private void handleRegionFile(File worldFolder, int x, int z, Set<ChunkLocation> result)
    {
        RegionFile regionFile = RegionFileCache.b(worldFolder, x << 5, z << 5);

        for ( int i = 0; i < 32; i++ )
        {
            for ( int j = 0; j < 32; j++ )
            {
                if ( regionFile.c(i, j) ) // should be: regionFile.chunkExists
                {
                    result.add(new ChunkLocation(x << 5 | i, z << 5 | j));
                }
            }
        }
    }
    
    private String[] listRegionFiles(File worldFolder)
    {
        File region = new File(worldFolder, "region");
        
        if ( !region.isDirectory() )
        {
            return new String[0];
        }
        
        return region.list();
    }
}
