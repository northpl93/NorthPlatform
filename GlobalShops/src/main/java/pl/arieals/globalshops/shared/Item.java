package pl.arieals.globalshops.shared;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class Item
{
    private ItemsGroup group;
    private String     id;

    public Item()
    {
    }

    public Item(final ItemsGroup group, final String id)
    {
        this.group = group;
        this.id = id;
    }

    public ItemsGroup getGroup()
    {
        return this.group;
    }

    public String getId()
    {
        return this.id;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("group", this.group).append("id", this.id).toString();
    }
}
