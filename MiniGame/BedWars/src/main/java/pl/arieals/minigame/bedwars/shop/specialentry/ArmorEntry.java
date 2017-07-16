package pl.arieals.minigame.bedwars.shop.specialentry;

import static java.text.MessageFormat.format;


import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class ArmorEntry implements IShopSpecialEntry
{
    @Override
    public boolean buy(final Player player, final Collection<ItemStack> items)
    {
        final ItemStack[] armorContents = player.getInventory().getArmorContents();
        for (final ItemStack item : items)
        {
            final ArmorType type = ArmorType.getType(item.getType());
            armorContents[type.ordinal()] = item;

            final ItemMeta itemMeta = item.getItemMeta();
            itemMeta.spigot().setUnbreakable(true);
            item.setItemMeta(itemMeta);
        }
        player.getInventory().setArmorContents(armorContents);
        return true;
    }
}

/**
 * Typ armoru wedlug kolejnosci z {@link PlayerInventory#getArmorContents()}
 */
enum ArmorType
{
    BOOTS,
    LEGGINGS,
    CHEST_PLATE,
    HELMET;

    static ArmorType getType(final Material material)
    {
        switch (material)
        {
            case LEATHER_HELMET:
            case IRON_HELMET:
            case GOLD_HELMET:
            case CHAINMAIL_HELMET:
            case DIAMOND_HELMET:
                return HELMET;

            case LEATHER_CHESTPLATE:
            case IRON_CHESTPLATE:
            case GOLD_CHESTPLATE:
            case CHAINMAIL_CHESTPLATE:
            case DIAMOND_CHESTPLATE:
                return CHEST_PLATE;

            case LEATHER_LEGGINGS:
            case IRON_LEGGINGS:
            case GOLD_LEGGINGS:
            case CHAINMAIL_LEGGINGS:
            case DIAMOND_LEGGINGS:
                return LEGGINGS;

            case LEATHER_BOOTS:
            case IRON_BOOTS:
            case GOLD_BOOTS:
            case CHAINMAIL_BOOTS:
            case DIAMOND_BOOTS:
                return BOOTS;

            default:
                throw new IllegalArgumentException(format("{0} isn't armor", material));
        }
    }
}