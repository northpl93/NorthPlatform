package pl.north93.zgame.antycheat.utils.block;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Material;

public enum BlockFlag
{
    /* Flaga oznaczająca że blok jest schodami */
    STAIRS,

    /* Flaga oznaczająca blok który jest połówką */
    HALF,

    /* Flaga oznaczająca blok po którym można się wspinać */
    CLIMBABLE,

    /* Flaga oznaczająca pajęczynę */
    COBWEB,

    /* Flaga oznaczająca że blok jest cieczą */
    LIQUID,
    WATER,
    LAVA;

    // IMPLEMENTACJA //

    private static final Map<Material, Long> materialFlags = new HashMap<>();
    static
    {
        // MASOWE USTAWIANIE FLAG
        for (final Material material : Material.values())
        {
            if (material.name().endsWith("_STAIRS"))
            {
                setFlags(material, BlockFlag.STAIRS);
            }
        }

        // POJEDYNCZE FLAGI
        setFlags(Material.STEP, BlockFlag.HALF);
        setFlags(Material.WOOD_STEP, BlockFlag.HALF);
        setFlags(Material.STONE_SLAB2, BlockFlag.HALF);
        setFlags(Material.PURPUR_SLAB, BlockFlag.HALF);

        setFlags(Material.VINE, BlockFlag.CLIMBABLE);
        setFlags(Material.LADDER, BlockFlag.CLIMBABLE);

        setFlags(Material.WEB, BlockFlag.COBWEB);

        setFlags(Material.WATER, BlockFlag.LIQUID, BlockFlag.WATER);
        setFlags(Material.STATIONARY_WATER, BlockFlag.LIQUID, BlockFlag.WATER);

        setFlags(Material.LAVA, BlockFlag.LIQUID, BlockFlag.LAVA);
        setFlags(Material.STATIONARY_LAVA, BlockFlag.LIQUID, BlockFlag.LAVA);
    }

    private static void setFlags(final Material material, final BlockFlag... flags)
    {
        long flagsMask = 0;
        for (final BlockFlag flag : flags)
        {
            flagsMask |= flag.getMask();
        }
        materialFlags.put(material, flagsMask);
    }

    public static long getFlags(final Material material)
    {
        return materialFlags.getOrDefault(material, 0L);
    }

    public static Collection<Material> getMaterialsWithFlag(final BlockFlag flag)
    {
        return materialFlags.entrySet().stream()
                            .filter(entry -> isFlagSet(entry.getValue(), flag))
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toList());
    }

    public long getMask()
    {
        return 1L << this.ordinal();
    }

    public static boolean isFlagSet(final long flags, final BlockFlag blockFlag)
    {
        return (flags & blockFlag.getMask()) != 0;
    }
}
