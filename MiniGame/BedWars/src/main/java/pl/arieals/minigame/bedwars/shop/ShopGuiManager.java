package pl.arieals.minigame.bedwars.shop;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.minigame.bedwars.cfg.BwShopEntry;
import pl.arieals.minigame.bedwars.shop.gui.ShopArmor;
import pl.arieals.minigame.bedwars.shop.gui.ShopBase;
import pl.arieals.minigame.bedwars.shop.gui.ShopBows;
import pl.arieals.minigame.bedwars.shop.gui.ShopExtras;
import pl.arieals.minigame.bedwars.shop.gui.ShopLapis;
import pl.arieals.minigame.bedwars.shop.gui.ShopMain;
import pl.arieals.minigame.bedwars.shop.gui.ShopMaterials;
import pl.arieals.minigame.bedwars.shop.gui.ShopSwords;
import pl.arieals.minigame.bedwars.shop.gui.ShopTools;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.uri.UriHandler;

public final class ShopGuiManager
{
    private final ShopManager shopManager;

    @Bean
    private ShopGuiManager(final ShopManager shopManager)
    {
        this.shopManager = shopManager;
    }

    @UriHandler("/minigame/bedwars/shopCategory/:name/:playerId")
    public boolean openCategory(final String calledUri, final Map<String, String> parameters)
    {
        final Player player = Bukkit.getPlayer(UUID.fromString(parameters.get("playerId")));

        final String name = parameters.get("name");
        final ShopBase gui;
        switch (name)
        {
            case "main":
                gui = new ShopMain(player);
                break;
            case "materials":
                gui = new ShopMaterials(player);
                break;
            case "tools":
                gui = new ShopTools(player);
                break;
            case "swords":
                gui = new ShopSwords(player);
                break;
            case "bows":
                gui = new ShopBows(player);
                break;
            case "armor":
                gui = new ShopArmor(player);
                break;
            case "extras":
                gui = new ShopExtras(player);
                break;
            case "lapis":
                gui = new ShopLapis(player);
                break;
            default:
                return false;
        }

        gui.open(player);

        return true;
    }

    @UriHandler("/minigame/bedwars/shop/nameColor/:name/:playerId")
    public String getNameColor(final String calledUri, final Map<String, String> parameters)
    {
        final Player player = Bukkit.getPlayer(UUID.fromString(parameters.get("playerId")));
        final String name = parameters.get("name");

        final ItemStack price = this.shopManager.getShopEntry(name).getPrice().createItemStack();
        if (player.getInventory().containsAtLeast(price, price.getAmount()))
        {
            return "a";
        }
        return "c";
    }

    @UriHandler("/minigame/bedwars/shop/lore/:name/:playerId")
    public String getLore(final String calledUri, final Map<String, String> parameters)
    {
        final Player player = Bukkit.getPlayer(UUID.fromString(parameters.get("playerId")));
        final String name = parameters.get("name");

        final BwShopEntry shopEntry = this.shopManager.getShopEntry(name);

        return "dupa, dokonczyc lore";
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
