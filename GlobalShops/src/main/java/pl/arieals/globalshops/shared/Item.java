package pl.arieals.globalshops.shared;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class Item
{
    private final ItemsGroup          group;
    private final String              id;
    private final int                 maxLevel;
    private final Rarity              rarity;
    private final Map<String, String> data;

    public Item(final ItemsGroup group, final String id, final int maxLevel, final Rarity rarity, final Map<String, String> data)
    {
        this.group = group;
        this.id = id;
        this.maxLevel = maxLevel;
        this.rarity = rarity;
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

    public Map<String, String> getData()
    {
        return this.data;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("group", this.group).append("id", this.id).append("maxLevel", this.maxLevel).append("rarity", this.rarity).append("data", this.data).toString();
    }
}
