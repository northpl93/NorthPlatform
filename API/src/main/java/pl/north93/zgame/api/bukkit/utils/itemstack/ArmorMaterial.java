package pl.north93.zgame.api.bukkit.utils.itemstack;

import org.bukkit.Material;

public enum ArmorMaterial
{
    LEATHER,
    IRON,
    CHAINMAIL,
    GOLD,
    DIAMOND;

    public static ArmorMaterial getArmorMaterial(final Material material)
    {
        switch (material)
        {
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
                return LEATHER;
            case IRON_HELMET:
            case IRON_CHESTPLATE:
            case IRON_LEGGINGS:
            case IRON_BOOTS:
                return IRON;
            case CHAINMAIL_HELMET:
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_LEGGINGS:
            case CHAINMAIL_BOOTS:
                return CHAINMAIL;
            case GOLD_HELMET:
            case GOLD_CHESTPLATE:
            case GOLD_LEGGINGS:
            case GOLD_BOOTS:
                return GOLD;
            case DIAMOND_HELMET:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
            case DIAMOND_BOOTS:
                return DIAMOND;
        }
        throw new IllegalArgumentException(material.toString());
    }
}
