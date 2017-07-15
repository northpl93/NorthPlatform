package pl.arieals.minigame.bedwars.shop.specialentry;

import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface IShopSpecialEntry
{
    boolean buy(Player player, Collection<ItemStack> items);
}
