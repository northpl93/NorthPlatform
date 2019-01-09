package pl.north93.zgame.skyplayerexp.server.gui;

import org.bukkit.entity.Player;

public interface IServerGuiManager
{
    void openServerMenu(Player player);

    void openServerOptions(Player player);

    void openIslandOptions(Player player);

    void openShopCategories(Player player);

    void openShopCategory(Player player, String category);

    void openVipShop(Player player);
}
