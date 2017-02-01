package pl.north93.zgame.skyblock.server;

import org.bukkit.block.Biome;

import pl.north93.zgame.skyblock.api.NorthBiome;

public final class BiomeMapper
{
    public static Biome toBukkit(final NorthBiome northBiome)
    {
        switch (northBiome)
        {
            case OVERWORLD:
                return Biome.PLAINS;
            case NETHER:
                return Biome.HELL;
            case THE_END:
                return Biome.SKY;
        }
        throw new RuntimeException("Unmapped biome: " + northBiome);
    }
}
