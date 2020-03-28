package pl.north93.northplatform.minigame.bedwars.shop.stattrack;

import org.bukkit.Material;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public enum TrackedWeapon
{
    WOODEN_SWORD(Material.WOOD_SWORD),
    STONE_SWORD(Material.STONE_SWORD),
    IRON_SWORD(Material.IRON_SWORD),
    DIAMOND_SWORD(Material.DIAMOND_SWORD),
    BOW(Material.BOW);

    private final Material material;

    TrackedWeapon(final Material material)
    {
        this.material = material;
    }

    public boolean isMatches(final Material material)
    {
        return this.material == material;
    }

    public static TrackedWeapon getByMaterial(final Material material)
    {
        for (final TrackedWeapon trackedWeapon : TrackedWeapon.values())
        {
            if (trackedWeapon.material == material)
            {
                return trackedWeapon;
            }
        }
        return null;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("material", this.material).toString();
    }
}
