package pl.north93.northplatform.lobby.gui;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.globalshops.server.IGlobalShops;
import pl.north93.northplatform.globalshops.server.IPlayerContainer;
import pl.north93.northplatform.globalshops.server.domain.IPrice;
import pl.north93.northplatform.globalshops.server.domain.Item;
import pl.north93.northplatform.globalshops.server.domain.ItemsGroup;
import pl.north93.northplatform.globalshops.server.impl.price.MoneyPrice;
import pl.north93.northplatform.globalshops.shared.GroupType;
import pl.north93.northplatform.api.bukkit.gui.Gui;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.LegacyMessage;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.messages.TranslatableString;
import pl.north93.northplatform.api.global.utils.Vars;

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
        builder.and("nameColor", ChatColor.GREEN);

        final Vars.Builder<Object> loreBuilder = Vars.builder();
        // rzadkosc przedmiotu
        loreBuilder.and("rarity", TranslatableString.of(generalMessages, "@rarity." + item.getRarity().name()));

        // shardy o obliczyc cene
        loreBuilder.and("price", getPrice(playerContainer, item));
        loreBuilder.and("shards", playerContainer.getShards(item));

        final String locale = this.player.getLocale();
        final Vars<Object> loreVars = loreBuilder.build();

        if (group.getGroupType() == GroupType.SINGLE_PICK)
        {
            if (item.equals(playerContainer.getActiveItem(group)))
            {
                // lore_selected
                builder.and("lore", TranslatableString.of(generalMessages, "@item.lore_selected$rarity").getLegacy(locale, loreVars));
            }
            else if (playerContainer.hasBoughtItem(item))
            {
                // lore_select
                builder.and("lore", TranslatableString.of(generalMessages, "@item.lore_select$rarity").getLegacy(locale, loreVars));
            }
            else
            {
                // lore_buy
                // todo sprawdzenie czy ma hajsy
                builder.and("lore", TranslatableString.of(generalMessages, "@item.lore_buy$rarity,price,shards").getLegacy(locale, loreVars));
            }
        }
        else if (group.getGroupType() == GroupType.MULTI_BUY)
        {
            if (playerContainer.hasMaxLevel(item))
            {
                // lore_bought
                builder.and("lore", TranslatableString.of(generalMessages, "@item.lore_bought$rarity").getLegacy(locale, loreVars));
            }
            else
            {
                // lore_upgrade
                builder.and("lore", TranslatableString.of(generalMessages, "@item.lore_upgrade$rarity,price,shards").getLegacy(locale, loreVars));
            }
        }

        return builder.build();
    }

    public static LegacyMessage getPrice(final IPlayerContainer container, final Item item)
    {
        final int level = Math.min(item.getMaxLevel(), container.getBoughtItemLevel(item) + 1);
        return getPrice(container, item, level);
    }
    
    public static LegacyMessage getPrice(final IPlayerContainer container, final Item item, final int level)
    {
        final Locale locale = container.getBukkitPlayer().getMyLocale();

        final IPrice price = item.getPrice(level);
        if (price instanceof MoneyPrice)
        {
            final MoneyPrice moneyPrice = (MoneyPrice) price;
            final int amount = (int) moneyPrice.getAmount(container, item);

            final DecimalFormat format = new DecimalFormat("#");
            if (moneyPrice.getDiscount(container, item) == 0)
            {
                final Vars<Object> vars = Vars.of("amount", format.format(amount));
                return TranslatableString.of(generalMessages, "@price.money.normal$amount").getLegacy(locale, vars);
            }
            else
            {
                final Vars.Builder<Object> builder = Vars.builder();
                builder.and("before", format.format(moneyPrice.getOriginalAmount()));
                builder.and("after", format.format(amount));
                builder.and("percent", format.format(moneyPrice.getDiscount(container, item) * 100));

                return TranslatableString.of(generalMessages, "@price.money.discounted$before,after,percent").getLegacy(locale, builder.build());
            }
        }
        else
        {
            return TranslatableString.of(generalMessages, "@price.free").getLegacy(locale, Vars.empty());
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("player", this.player).append("categoryName", this.categoryName).toString();
    }
}
