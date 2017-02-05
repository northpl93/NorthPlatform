package pl.north93.zgame.skyblock.shop.api;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import pl.north93.zgame.skyblock.shop.cfg.BukkitItem;

public interface ISpawnerEntry
{
    String getDisplayName();

    EntityType getMobEntityType();

    Double getPrice();

    ItemStack getRepresentingItem();
}
