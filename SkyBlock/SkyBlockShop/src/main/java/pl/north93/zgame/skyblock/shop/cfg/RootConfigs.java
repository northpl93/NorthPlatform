package pl.north93.zgame.skyblock.shop.cfg;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.CfgComment;
import org.diorite.cfg.annotations.defaults.CfgDelegateDefault;
import org.diorite.cfg.annotations.defaults.CfgStringDefault;

public final class RootConfigs
{
    @CfgComment("Konfiguracja pluginu dodajacego sklepy")
    public static final class ShopConfig
    {
        @CfgComment("Nazwa waluty uzywanej przez plugin.")
        @CfgStringDefault("setup")
        private String               currencyName;

        @CfgComment("Lista kategorii w sklepie")
        @CfgDelegateDefault("{ArrayList}")
        private List<CategoryConfig> categories;

        public String getCurrencyName()
        {
            return this.currencyName;
        }

        public List<CategoryConfig> getCategories()
        {
            //sort categories by it's internal name
            this.categories.sort((cat1, cat2) -> cat1.getInternalName().compareTo(cat2.getInternalName()));
            return this.categories;
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("currencyName", this.currencyName).append("categories", this.categories).toString();
        }
    }

    @CfgComment("Konfiguracja kategorii")
    public static final class ShopEntriesConfig
    {
        @CfgDelegateDefault("{ArrayList}")
        private List<ShopEntryConfig> items;

        public List<ShopEntryConfig> getItems()
        {
            return this.items;
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("items", this.items).toString();
        }
    }

    @CfgComment("Konfiguracja spawnera")
    public static final class SpawnerConfig
    {
        @CfgDelegateDefault("{ArrayList}")
        private List<SpawnerEntryConfig> mobs;

        public List<SpawnerEntryConfig> getMobs() { return this.mobs; }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("mobs", this.mobs).toString();
        }
    }
}
