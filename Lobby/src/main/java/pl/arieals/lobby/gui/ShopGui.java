package pl.arieals.lobby.gui;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.globalshops.server.IGlobalShops;
import pl.arieals.globalshops.server.IPlayerContainer;
import pl.arieals.globalshops.shared.GroupType;
import pl.arieals.globalshops.shared.Item;
import pl.arieals.globalshops.shared.ItemsGroup;
import pl.north93.zgame.api.bukkit.gui.Gui;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.messages.TranslatableString;
import pl.north93.zgame.api.global.utils.Vars;

public abstract class ShopGui extends Gui
{
    @Inject @Messages("ShopGeneral")
    private static MessagesBox generalMessages;
    @Inject
    private static IGlobalShops globalShops;
    protected final Player       player;
    protected final String       categoryName;

    protected ShopGui(final MessagesBox messagesBox, final String layout, final Player player, final String itemsCategory)
    {
        super(messagesBox, layout);
        this.player = player;
        this.categoryName = itemsCategory;
    }

    @Override
    protected void onRender()
    {
        final IPlayerContainer playerContainer = globalShops.getPlayer(this.player);
        final ItemsGroup group = globalShops.getGroup(this.categoryName);

        for (final Item item : group.getItems())
        {
            final Vars<Object> vars = this.generateItemVars(playerContainer, item);
            for (final Map.Entry<String, Object> var : vars.asMap().entrySet())
            {
                this.addVariables(Vars.of(var.getKey() + "-" + item.getId(), var.getValue()));
            }
        }
    }

    private Vars<Object> generateItemVars(final IPlayerContainer playerContainer, final Item item)
    {
        final ItemsGroup group = item.getGroup();
        Vars<Object> vars = Vars.empty();

        // nazwa przedmiotu
        vars = vars.and("name", item.getName());
        //vars = vars.and("name", TranslatableString.constant(item.getName(Locale.forLanguageTag(this.player.spigot().getLocale()))));
        vars = vars.and("nameColor", ChatColor.GREEN);

        Vars<Object> loreVars = Vars.empty();
        // rzadkosc przedmiotu
        loreVars = loreVars.and("rarity", TranslatableString.of(generalMessages, "@rarity." + item.getRarity()));

        // shardy o obliczyc cene
        loreVars = loreVars.and("price", "price todo");
        loreVars = loreVars.and("shards", playerContainer.getShards(item));

        if (group.getGroupType() == GroupType.SINGLE_PICK)
        {
            if (item.equals(playerContainer.getActiveItem(group)))
            {
                // lore_selected
                vars = vars.and("lore", TranslatableString.of(generalMessages, "@item.lore_selected$rarity").getValue(this.player, loreVars));
            }
            else if (playerContainer.hasBoughtItem(item))
            {
                // lore_select
                vars = vars.and("lore", TranslatableString.of(generalMessages, "@item.lore_select$rarity").getValue(this.player, loreVars));
            }
            else
            {
                // lore_buy
                // todo sprawdzenie czy ma hajsy
                vars = vars.and("lore", TranslatableString.of(generalMessages, "@item.lore_buy$rarity,price,shards").getValue(this.player, loreVars));
            }
        }
        else if (group.getGroupType() == GroupType.MULTI_BUY)
        {
            if (playerContainer.hasMaxLevel(item))
            {
                // najwyzszy level kupiony
            }
            else
            {
                vars = vars.and("lore", TranslatableString.of(generalMessages, "@item.lore_upgrade$rarity,price,shards").getValue(this.player, loreVars));
                // lore_upgrade
            }
        }

        return vars;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("player", this.player).append("categoryName", this.categoryName).toString();
    }
}
