package pl.arieals.globalshops.server.domain;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.globalshops.shared.GroupType;

/**
 * Przedstawia grupe przedmiotow.
 */
public final class ItemsGroup
{
    private final String     id;
    private final GroupType  groupType;
    private final List<Item> items;

    public ItemsGroup(final String id, final GroupType groupType, final List<Item> items)
    {
        this.id = id;
        this.groupType = groupType;
        this.items = items;
    }

    public String getId()
    {
        return this.id;
    }

    public GroupType getGroupType()
    {
        return this.groupType;
    }

    public List<Item> getItems()
    {
        return this.items;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("id", this.id).append("groupType", this.groupType).append("items", this.items).toString();
    }
}
