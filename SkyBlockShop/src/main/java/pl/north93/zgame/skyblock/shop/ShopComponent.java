package pl.north93.zgame.skyblock.shop;

import static pl.north93.zgame.api.global.cfg.ConfigUtils.loadConfigFile;


import java.io.File;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.economy.ICurrency;
import pl.north93.zgame.api.economy.impl.client.EconomyComponent;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.skyblock.shop.api.ICategory;
import pl.north93.zgame.skyblock.shop.api.IShopEntry;
import pl.north93.zgame.skyblock.shop.cfg.CategoryConfig;
import pl.north93.zgame.skyblock.shop.cfg.RootConfigs.ShopConfig;
import pl.north93.zgame.skyblock.shop.cfg.RootConfigs.ShopEntriesConfig;
import pl.north93.zgame.skyblock.shop.cfg.ShopEntryConfig;

public class ShopComponent extends Component
{
    @Inject
    private EconomyComponent economyComponent;
    private ShopManager      shopManager;

    @Override
    protected void enableComponent()
    {
        final File shop = this.getApiCore().getFile("shop");
        if (! shop.exists())
        {
            shop.mkdir();
        }

        final ShopConfig shopConfig = loadConfigFile(ShopConfig.class, new File(shop, "config.yml"));
        final ICurrency currency = this.economyComponent.getEconomyManager().getCurrency(shopConfig.getCurrencyName());

        this.shopManager = new ShopManager(currency, this.load(shop, shopConfig));
    }

    private Multimap<ICategory, IShopEntry> load(final File shopDir, final ShopConfig shopConfig)
    {
        final Multimap<ICategory, IShopEntry> map = ArrayListMultimap.create();

        for (final CategoryConfig categoryConfig : shopConfig.getCategories())
        {
            final ShopEntriesConfig shopEntries = loadConfigFile(ShopEntriesConfig.class, new File(shopDir, categoryConfig.getFileName()));
            for (final ShopEntryConfig shopEntryConfig : shopEntries.getItems())
            {
                map.put(categoryConfig, shopEntryConfig);
            }
        }

        return map;
    }

    @Override
    protected void disableComponent()
    {
    }

    public ShopManager getShopManager()
    {
        return this.shopManager;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("shopManager", this.shopManager).toString();
    }
}
