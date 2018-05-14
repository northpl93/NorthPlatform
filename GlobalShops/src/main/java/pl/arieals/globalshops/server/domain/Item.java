package pl.arieals.globalshops.server.domain;

import java.util.Locale;
import java.util.Map;

import com.carrotsearch.hppc.IntObjectMap;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.globalshops.shared.Rarity;
import pl.north93.zgame.api.global.messages.TranslatableString;

public final class Item
{
    private final ItemsGroup           group;
    private final String               id;
    private final int                  maxLevel;
    private final Rarity               rarity;
    private final IntObjectMap<IPrice> price;
    private final Map<Locale, String>  name;
    private final Map<String, String>  data;

    public Item(final ItemsGroup group, final String id, final int maxLevel, final Rarity rarity, final IntObjectMap<IPrice> price, final Map<Locale, String> name, final Map<String, String> data)
    {
        this.group = group;
        this.id = id;
        this.maxLevel = maxLevel;
        this.rarity = rarity;
        this.price = price;
        this.name = name;
        this.data = ImmutableMap.copyOf(data);
    }

    public ItemsGroup getGroup()
    {
        return this.group;
    }

    public String getId()
    {
        return this.id;
    }

    public int getMaxLevel()
    {
        return this.maxLevel;
    }

    public Rarity getRarity()
    {
        return this.rarity;
    }

    public IntObjectMap<IPrice> getPrices()
    {
        return this.price;
    }

    public IPrice getPrice(final int level)
    {
        Preconditions.checkState(this.maxLevel >= level);
        return this.price.get(level);
    }

    /**
     * Zwraca nazwe tego przedmiotu jako {@link TranslatableString}.
     * Zestaw tlumaczen pobierany jest z configu.
     *
     * @return Nazwa przedmiotu jako tlumaczalny lancuch tekstowy.
     */
    public TranslatableString getName()
    {
        return TranslatableString.custom(this.name);
    }

    public String getName(final Locale locale)
    {
        return this.name.getOrDefault(locale, this.id);
    }

    public Map<String, String> getData()
    {
        return this.data;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("group", this.group).append("id", this.id).append("maxLevel", this.maxLevel).append("rarity", this.rarity).append("name", this.name).append("data", this.data).toString();
    }
}
