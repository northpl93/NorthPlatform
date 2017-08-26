package pl.north93.zgame.api.bukkit.utils.itemstack;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Material;

public class MaterialUtils
{
    private static final Material[] SWORD_MATERIALS = { Material.WOOD_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD };
    
    private MaterialUtils()
    {
    }
    
    public static boolean isSword(Material material)
    {
        return ArrayUtils.contains(SWORD_MATERIALS, material);
    }
}
