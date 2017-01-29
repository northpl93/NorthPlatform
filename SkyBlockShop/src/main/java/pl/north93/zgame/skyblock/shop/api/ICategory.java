package pl.north93.zgame.skyblock.shop.api;

import pl.north93.zgame.skyblock.shop.cfg.BukkitItem;

public interface ICategory
{
    String getInternalName();

    String getFileName();

    String getDisplayName();

    BukkitItem getRepresentingItem();

    String getPermission();
}
