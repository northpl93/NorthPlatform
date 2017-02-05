package pl.north93.zgame.skyplayerexp.server.gui;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.skyblock.shop.ShopComponent;
import pl.north93.zgame.skyblock.shop.ShopManager;
import pl.north93.zgame.skyblock.shop.api.ICategory;
import pl.north93.zgame.skyplayerexp.server.ExperienceServer;

public class ServerGuiManager implements IServerGuiManager
{
    @InjectComponent("SkyBlock.PlayerExperience.Server")
    private ExperienceServer experience;
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager  networkManager;
    @InjectComponent("SkyBlock.Shop.Server")
    private ShopComponent    shopComponent;
    private BukkitApiCore    apiCore;

    @Override
    public void openServerMenu(final Player player)
    {
        final ServerMenu window = new ServerMenu(this, player);
        this.apiCore.getWindowManager().openWindow(player, window);
        this.apiCore.getPlatformConnector().runTaskAsynchronously(() ->
        {
            window.loadAsyncData(this.networkManager.getOnlinePlayer(player.getName()));
        });
    }

    @Override
    public void openServerOptions(final Player player)
    {
        final PlayerOptions window = new PlayerOptions(this.experience, player);
        this.apiCore.getWindowManager().openWindow(player, window);
        this.apiCore.getPlatformConnector().runTaskAsynchronously(() ->
        {
            window.loadAsyncData(this.networkManager.getOnlinePlayer(player.getName()));
        });
    }

    @Override
    public void openIslandOptions(final Player player)
    {
        final IslandOptions window = new IslandOptions(this.experience, player);
        this.apiCore.getWindowManager().openWindow(player, window);
        this.apiCore.getPlatformConnector().runTaskAsynchronously(() ->
        {
            window.loadAsyncData(this.networkManager.getOnlinePlayer(player.getName()));
        });
    }

    @Override
    public void openShopCategories(final Player player)
    {
        this.apiCore.getWindowManager().openWindow(player, new ShopCategories(this, player));
    }

    @Override
    public void openShopCategory(final Player player, final String category)
    {
        final ShopManager shopManager = this.shopComponent.getShopManager();
        final ICategory categoryObj = shopManager.getCategory(category);
        shopManager.openCategory(categoryObj, player, this::openShopCategories);
    }

    @Override
    public void openVipShop(final Player player)
    {
        final ShopManager shopManager = this.shopComponent.getShopManager();
        final ICategory vip = shopManager.getCategory("sklep_vip");
        shopManager.openCategory(vip, player, this::openShopCategories);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
