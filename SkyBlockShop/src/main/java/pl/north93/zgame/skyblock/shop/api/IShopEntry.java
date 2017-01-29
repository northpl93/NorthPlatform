package pl.north93.zgame.skyblock.shop.api;

import pl.north93.zgame.skyblock.shop.cfg.BukkitItem;

public interface IShopEntry
{
    String getDisplayName();

    int getAmount();

    BukkitItem getBukkitItem();

    boolean canBuy();

    boolean canSell();

    Double getBuyPrice();

    Double getSellPrice();
}
