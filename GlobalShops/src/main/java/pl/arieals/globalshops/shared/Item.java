package pl.arieals.globalshops.shared;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class Item
{
    private ItemsGroup          group;
    private String              id;
    private Map<String, String> data;

    public Item()
    {
    }

    public Item(final ItemsGroup group, final String id, final Map<String, String> data)
    {
        this.group = group;
        this.id = id;
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

    public Map<String, String> getData()
    {
        return this.data;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("group", this.group).append("id", this.id).append("data", this.data).toString();
    }
}
