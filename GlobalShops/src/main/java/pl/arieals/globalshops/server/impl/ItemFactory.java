package pl.arieals.globalshops.server.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.globalshops.controller.cfg.ItemCfg;
import pl.arieals.globalshops.controller.cfg.ItemDataCfg;
import pl.arieals.globalshops.controller.cfg.ItemName;
import pl.arieals.globalshops.controller.cfg.ItemPriceCfg;
import pl.arieals.globalshops.controller.cfg.ItemsGroupCfg;
import pl.arieals.globalshops.server.domain.IPrice;
import pl.arieals.globalshops.server.domain.Item;
import pl.arieals.globalshops.server.domain.ItemsGroup;
import pl.arieals.globalshops.server.impl.price.MoneyPrice;
import pl.arieals.globalshops.server.impl.price.NullPrice;
import pl.north93.zgame.api.economy.IEconomyManager;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

/*default*/ class ItemFactory
{
    @Inject
    private IEconomyManager economyManager;

    @Bean
    private ItemFactory()
    {
    }

    public ItemsGroup createGroupFromConfig(final ItemsGroupCfg cfg)
    {
        final List<Item> items = new ArrayList<>();
        final ItemsGroup itemsGroup = new ItemsGroup(cfg.getId(), cfg.getGroupType(), items);

        for (final ItemCfg itemCfg : cfg.getItems())
        {
            items.add(this.createItemFromConfig(itemsGroup, itemCfg));
        }

        return itemsGroup;
    }

    private Item createItemFromConfig(final ItemsGroup group, final ItemCfg cfg)
    {
        final Map<Locale, String> name = cfg.getNames().stream().collect(Collectors.toMap(itemName -> Locale.forLanguageTag(itemName.getLang()), ItemName::getName));
        final Map<String, String> itemData = cfg.getItemData().stream().collect(Collectors.toMap(ItemDataCfg::getName, ItemDataCfg::getValue));

        final IntObjectMap<IPrice> prices = this.createPrices(cfg);

        return new Item(group, cfg.getId(), cfg.getMaxLevel(), cfg.getRarity(), prices, name, itemData);
    }

    private IntObjectMap<IPrice> createPrices(final ItemCfg cfg)
    {
        final IntObjectMap<IPrice> prices = new IntObjectHashMap<>();

        final List<ItemPriceCfg> cfgPrices = cfg.getPrices();
        if (cfgPrices.isEmpty())
        {
            prices.put(1, this.createPriceFromConfig(null));
        }
        else
        {
            for (final ItemPriceCfg itemPriceCfg : cfg.getPrices())
            {
                prices.put(itemPriceCfg.getLevel(), this.createPriceFromConfig(itemPriceCfg));
            }
        }

        return prices;
    }

    private IPrice createPriceFromConfig(final ItemPriceCfg cfg)
    {
        if (cfg == null)
        {
            return NullPrice.INSTANCE;
        }
        return new MoneyPrice(cfg, this.economyManager);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}