package pl.north93.northplatform.minigame.bedwars.shop;

import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getArena;


import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.minigame.bedwars.cfg.BwShopEntry;
import pl.north93.northplatform.minigame.bedwars.event.ItemPreBuyEvent;
import pl.north93.northplatform.minigame.bedwars.shop.gui.ShopArmor;
import pl.north93.northplatform.minigame.bedwars.shop.gui.ShopBase;
import pl.north93.northplatform.minigame.bedwars.shop.gui.ShopBows;
import pl.north93.northplatform.minigame.bedwars.shop.gui.ShopExtras;
import pl.north93.northplatform.minigame.bedwars.shop.gui.ShopLapis;
import pl.north93.northplatform.minigame.bedwars.shop.gui.ShopMain;
import pl.north93.northplatform.minigame.bedwars.shop.gui.ShopMaterials;
import pl.north93.northplatform.minigame.bedwars.shop.gui.ShopSwords;
import pl.north93.northplatform.minigame.bedwars.shop.gui.ShopTools;
import pl.north93.northplatform.api.bukkit.BukkitApiCore;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.utils.chat.ChatUtils;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.messages.PluralForm;
import pl.north93.northplatform.api.global.uri.UriHandler;
import pl.north93.northplatform.api.global.uri.UriInvocationContext;

public final class ShopGuiManager
{
    private final BukkitApiCore apiCore;
    private final ShopManager   shopManager;
    private final MessagesBox   shopMessages;

    @Bean
    private ShopGuiManager(final BukkitApiCore apiCore, final ShopManager shopManager, final @Messages("BedWarsShop") MessagesBox shopMessages)
    {
        this.apiCore = apiCore;
        this.shopManager = shopManager;
        this.shopMessages = shopMessages;
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
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.4f, 1); // volume, pitch

        return true;
    }

    @UriHandler("/minigame/bedwars/shop/nameColor/:name/:playerId")
    public String getNameColor(final UriInvocationContext context)
    {
        final INorthPlayer player = INorthPlayer.get(context.asUuid("playerId"));
        final String name = context.asString("name");

        final BwShopEntry shopEntry = this.shopManager.getShopEntry(name);
        final ItemStack price = shopEntry.getPrice().createItemStack();

        final ItemPreBuyEvent preBuyEvent = this.apiCore.callEvent(new ItemPreBuyEvent(getArena(player), player, shopEntry, price, true));
        if (preBuyEvent.getBuyStatus().canBuy())
        {
            return ChatUtils.COLOR_CHAR + "a";
        }
        return ChatUtils.COLOR_CHAR + "c";
    }

    @UriHandler("/minigame/bedwars/shop/lore/:name/:playerId")
    public String getLore(final UriInvocationContext context)
    {
        final INorthPlayer player = INorthPlayer.get(context.asUuid("playerId"));
        final String name = context.asString("name");
        final String locale = player.getLocale();

        final BwShopEntry shopEntry = this.shopManager.getShopEntry(name);
        final ItemStack priceItem = shopEntry.getPrice().createItemStack();

        final String currencyKey = "currency." + priceItem.getType().name().toLowerCase(Locale.ROOT);
        final String priceMsgKey = PluralForm.transformKey(currencyKey, priceItem.getAmount());
        final String price = this.shopMessages.getString(locale, priceMsgKey, priceItem.getAmount());

        final String description = this.shopMessages.getString(locale, "item." + name + ".lore");

        final ItemPreBuyEvent preBuyEvent = this.apiCore.callEvent(new ItemPreBuyEvent(getArena(player), player, shopEntry, priceItem, true));
        final ItemPreBuyEvent.BuyStatus buyStatus = preBuyEvent.getBuyStatus();

        if (buyStatus.canBuy())
        {
            return this.shopMessages.getString(locale,
                    "gui.shop.item_lore.available",
                    description,
                    price);
        }
        else if (buyStatus == ItemPreBuyEvent.BuyStatus.NOT_ENOUGH_CURRENCY)
        {
            final String currencyName = ChatUtils.stripColor(this.shopMessages.getString(locale, currencyKey + ".many", ""));
            return this.shopMessages.getString(locale,
                    "gui.shop.item_lore.no_money",
                    description,
                    price,
                    currencyName);
        }
        else
        {
            return this.shopMessages.getString(locale,
                    "gui.shop.item_lore.already_had",
                    description,
                    price);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
