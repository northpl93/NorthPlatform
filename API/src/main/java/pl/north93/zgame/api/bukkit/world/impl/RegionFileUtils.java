package pl.north93.zgame.api.bukkit.world.impl;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.World;

import net.minecraft.server.v1_12_R1.RegionFile;
import net.minecraft.server.v1_12_R1.RegionFileCache;

import pl.north93.zgame.api.bukkit.utils.xml.XmlChunk;

class RegionFileUtils
{    
    static Set<XmlChunk> getGeneratedChunks(World bukkitWorld)
    {
        return getGeneratedChunks(bukkitWorld.getWorldFolder());
    }
    
    static Set<XmlChunk> getGeneratedChunks(File worldFolder)
    {
        Set<XmlChunk> result = new HashSet<>();
        
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
    
    private static void handleRegionFile(File worldFolder, int x, int z, Set<XmlChunk> result)
    {
        RegionFile regionFile = RegionFileCache.b(worldFolder, x << 5, z << 5);

        for ( int i = 0; i < 32; i++ )
        {
            for ( int j = 0; j < 32; j++ )
            {
                if ( regionFile.c(i, j) ) // should be: regionFile.chunkExists
                {
                    result.add(new XmlChunk(x << 5 | i, z << 5 | j));
                }
            }
        }
    }
    
    private static String[] listRegionFiles(File worldFolder)
    {
        File region = new File(worldFolder, "region");
        
        if ( !region.isDirectory() )
        {
            return new String[0];
        }
        
        return region.list();
    }
}
