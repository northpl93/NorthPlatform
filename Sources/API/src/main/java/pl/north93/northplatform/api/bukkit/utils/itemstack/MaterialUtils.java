package pl.north93.northplatform.api.bukkit.utils.itemstack;

import org.bukkit.Material;

import org.apache.commons.lang3.ArrayUtils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MaterialUtils
{
    private final Material[] SWORD_MATERIALS = { Material.WOOD_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD };
    
    public boolean isSword(final Material material)
    {
        return ArrayUtils.contains(SWORD_MATERIALS, material);
    }
}
