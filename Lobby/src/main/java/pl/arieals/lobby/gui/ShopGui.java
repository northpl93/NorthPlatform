package pl.arieals.lobby.gui;

import java.text.DecimalFormat;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.chat.BaseComponent;
import pl.arieals.globalshops.server.IGlobalShops;
import pl.arieals.globalshops.server.IPlayerContainer;
import pl.arieals.globalshops.server.domain.IPrice;
import pl.arieals.globalshops.server.domain.Item;
import pl.arieals.globalshops.server.domain.ItemsGroup;
import pl.arieals.globalshops.server.impl.price.MoneyPrice;
import pl.arieals.globalshops.shared.GroupType;
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
                this.getContent().addVariables(Vars.of(var.getKey() + "-" + item.getId(), var.getValue()));
            }
        }
    }

    private Vars<Object> generateItemVars(final IPlayerContainer playerContainer, final Item item)
    {
        final ItemsGroup group = item.getGroup();
        final Vars.Builder<Object> builder = Vars.builder();

        // nazwa przedmiotu
        builder.and("name", item.getName());
        //vars = vars.and("name", TranslatableString.constant(item.getName(Locale.forLanguageTag(this.player.spigot().getLocale()))));
        builder.and("nameColor", ChatColor.GREEN);

        final Vars.Builder<Object> loreBuilder = Vars.builder();
        // rzadkosc przedmiotu
        loreBuilder.and("rarity", TranslatableString.of(generalMessages, "@rarity." + item.getRarity().name()));

        // shardy o obliczyc cene
        loreBuilder.and("price", this.getPrice(playerContainer, item));
        loreBuilder.and("shards", playerContainer.getShards(item));

        final Vars<Object> loreVars = loreBuilder.build();
        if (group.getGroupType() == GroupType.SINGLE_PICK)
        {
            if (item.equals(playerContainer.getActiveItem(group)))
            {
                // lore_selected
                builder.and("lore", TranslatableString.of(generalMessages, "@item.lore_selected$rarity").getValue(this.player, loreVars));
            }
            else if (playerContainer.hasBoughtItem(item))
            {
                // lore_select
                builder.and("lore", TranslatableString.of(generalMessages, "@item.lore_select$rarity").getValue(this.player, loreVars));
            }
            else
            {
                // lore_buy
                // todo sprawdzenie czy ma hajsy
                builder.and("lore", TranslatableString.of(generalMessages, "@item.lore_buy$rarity,price,shards").getValue(this.player, loreVars));
            }
        }
        else if (group.getGroupType() == GroupType.MULTI_BUY)
        {
            if (playerContainer.hasMaxLevel(item))
            {
                // lore_bought
                builder.and("lore", TranslatableString.of(generalMessages, "@item.lore_bought$rarity").getValue(this.player, loreVars));
            }
            else
            {
                // lore_upgrade
                builder.and("lore", TranslatableString.of(generalMessages, "@item.lore_upgrade$rarity,price,shards").getValue(this.player, loreVars));
            }
        }

        return builder.build();
    }

    private BaseComponent getPrice(final IPlayerContainer container, final Item item)
    {
        final int level = Math.min(item.getMaxLevel(), container.getBoughtItemLevel(item) + 1);

        final IPrice price = item.getPrice(level);
        if (price instanceof MoneyPrice)
        {
            final MoneyPrice moneyPrice = (MoneyPrice) price;
            final int amount = (int) moneyPrice.getAmount(container, item);

            if (moneyPrice.getDiscount(container, item) == 0)
            {
                final Vars<Object> vars = Vars.of("amount", amount);
                return TranslatableString.of(generalMessages, "@price.money.normal$amount").getValue(this.player, vars);
            }
            else
            {
                final DecimalFormat format = new DecimalFormat("#");
                Vars<Object> vars = Vars.of("before", format.format(moneyPrice.getOriginalAmount()));
                vars = vars.and("after", amount);
                vars = vars.and("percent", format.format(moneyPrice.getDiscount(container, item) * 100));

                return TranslatableString.of(generalMessages, "@price.money.discounted$before,after,percent").getValue(this.player, vars);
            }
        }
        else
        {
            return TranslatableString.of(generalMessages, "@price.free").getValue(this.player, Vars.empty());
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("player", this.player).append("categoryName", this.categoryName).toString();
    }
}
