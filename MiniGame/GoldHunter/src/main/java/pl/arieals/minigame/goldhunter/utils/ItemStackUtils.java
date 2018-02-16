package pl.arieals.minigame.goldhunter.utils;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class ItemStackUtils
{
    private ItemStackUtils()
    {
    }
    
    public static ItemStack hideAttributesAndMakeUnbreakable(ItemStack is)
    {
        ItemMeta itemMeta = is.getItemMeta();
        itemMeta.setUnbreakable(true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        is.setItemMeta(itemMeta);
        return is;
    }
}
