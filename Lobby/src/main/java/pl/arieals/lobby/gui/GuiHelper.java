package pl.arieals.lobby.gui;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.globalshops.server.IGlobalShops;
import pl.arieals.globalshops.server.IPlayerContainer;
import pl.arieals.globalshops.server.IPlayerExperienceService;
import pl.arieals.globalshops.shared.GroupType;
import pl.arieals.globalshops.shared.Item;
import pl.arieals.globalshops.shared.ItemsGroup;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.gui.Gui;
import pl.north93.zgame.api.bukkit.gui.IGuiManager;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.messages.TranslatableString;
import pl.north93.zgame.api.global.uri.UriHandler;
import pl.north93.zgame.api.global.utils.Vars;

public class GuiHelper
{
    @Inject @Messages("ShopGeneral")
    private MessagesBox  generalMessages;
    @Inject
    private BukkitApiCore apiCore;
    @Inject
    private IGlobalShops globalShops;
    @Inject
    private IGuiManager  guiManager;
    @Inject
    private IPlayerExperienceService playerExperienceService;

    @Bean
    private GuiHelper()
    {
    }

    @UriHandler("/lobby/shop/general/categoryVars/:categoryName/:playerId")
    public Vars<Object> generateCategoryInfo(final String calledUri, final Map<String, String> parameters)
    {
        final ItemsGroup itemsGroup = this.globalShops.getGroup(parameters.get("categoryName"));
        final Player player = Bukkit.getPlayer(UUID.fromString(parameters.get("playerId")));
        final IPlayerContainer playerContainer = this.globalShops.getPlayer(player);

        final int itemsBought = playerContainer.getBoughtItems(itemsGroup).size();
        final int itemsAmount = itemsGroup.getItems().size();

        Vars<Object> vars = Vars.empty();

        vars = vars.and("boughtItems", itemsBought + "/" + itemsAmount);
        if (itemsGroup.getGroupType() == GroupType.SINGLE_PICK)
        {
            final Item activeItem = playerContainer.getActiveItem(itemsGroup);
            if (activeItem == null)
            {
                vars = vars.and("selected", "Domyslny");
            }
            else
            {
                vars = vars.and("selected", activeItem.getName(Locale.forLanguageTag(player.spigot().getLocale())));
            }
        }

        return vars;
    }

    @UriHandler("/lobby/shop/general/itemVars/:categoryName/:itemName/:playerId")
    public Vars<Object> generateItemInfo(final String calledUri, final Map<String, String> parameters)
    {
        final ItemsGroup itemsGroup = this.globalShops.getGroup(parameters.get("categoryName"));
        final Item item = this.globalShops.getItem(itemsGroup, parameters.get("itemName"));

        final Player player = Bukkit.getPlayer(UUID.fromString(parameters.get("playerId")));
        final IPlayerContainer playerContainer = this.globalShops.getPlayer(player);
        Vars<Object> vars = Vars.empty();

        // nazwa przedmiotu
        vars = vars.and("name", TranslatableString.constant(item.getName(Locale.forLanguageTag(player.spigot().getLocale()))));
        vars = vars.and("nameColor", ChatColor.GREEN);

        // rzadkosc przedmiotu
        vars = vars.and("rarity", TranslatableString.of(this.generalMessages, "@rarity." + item.getRarity()));

        // shardy o obliczyc cene
        vars = vars.and("price", "price todo");
        vars = vars.and("shards", "shards todo");

        if (item.getGroup().getGroupType() == GroupType.SINGLE_PICK)
        {
            if (item.equals(playerContainer.getActiveItem(itemsGroup)))
            {
                vars = vars.and("lore", TranslatableString.of(this.generalMessages, "@item.lore_selected$rarity"));
                // lore_selected
            }
            else if (playerContainer.hasBoughtItem(item))
            {
                vars = vars.and("lore", TranslatableString.of(this.generalMessages, "@item.lore_select$rarity"));
                // lore_select
            }
            else
            {
                vars = vars.and("lore", TranslatableString.of(this.generalMessages, "@item.lore_buy$rarity,price,shards"));
                // lore_buy
                // todo sprawdzenie czy ma hajsy
            }
        }
        else if (item.getGroup().getGroupType() == GroupType.MULTI_BUY)
        {
            if (playerContainer.hasMaxLevel(item))
            {
                // najwyzszy level kupiony
            }
            else
            {
                vars = vars.and("lore", TranslatableString.of(this.generalMessages, "@item.lore_upgrade$rarity,price,shards"));
                // lore_upgrade
            }
        }

        return vars;
    }

    @UriHandler("/lobby/shop/general/click/:categoryName/:itemName/:playerId")
    public void handleClick(final String calledUri, final Map<String, String> parameters)
    {
        this.apiCore.sync(() ->
        {
            final ItemsGroup itemsGroup = this.globalShops.getGroup(parameters.get("categoryName"));
            final Item item = this.globalShops.getItem(itemsGroup, parameters.get("itemName"));
            final Player player = Bukkit.getPlayer(UUID.fromString(parameters.get("playerId")));

            final IPlayerContainer playerContainer = this.globalShops.getPlayer(player);
            this.playerExperienceService.processClick(playerContainer, item);
            return player;
        }, this::markGuiDirty);
    }

    @UriHandler("/lobby/shop/general/default/:categoryName/:playerId")
    public void handleDefault(final String calledUri, final Map<String, String> parameters)
    {
        this.apiCore.sync(() ->
        {
            final ItemsGroup itemsGroup = this.globalShops.getGroup(parameters.get("categoryName"));
            final Player player = Bukkit.getPlayer(UUID.fromString(parameters.get("playerId")));
            final IPlayerContainer playerContainer = this.globalShops.getPlayer(player);

            playerContainer.resetActiveItem(itemsGroup);
            return player;
        }, this::markGuiDirty);
    }

    private void markGuiDirty(final Player player)
    {
        final Gui currentGui = this.guiManager.getCurrentGui(player);
        if (currentGui != null)
        {
            currentGui.markDirty();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
