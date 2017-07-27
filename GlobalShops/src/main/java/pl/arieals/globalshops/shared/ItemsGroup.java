package pl.arieals.globalshops.shared;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.globalshops.controller.cfg.ItemCfg;
import pl.arieals.globalshops.controller.cfg.ItemsGroupCfg;

/**
 * Przedstawia grupe przedmiotow.
 */
public final class ItemsGroup
{
    private String     id;
    private GroupType  groupType;
    private List<Item> items;

    public ItemsGroup() // serializarion
    {
    }

    public ItemsGroup(final ItemsGroupCfg cfg)
    {
        this.id = cfg.getId();
        this.groupType = cfg.getGroupType();
        this.items = new ArrayList<>();
        for (final ItemCfg itemCfg : cfg.getItems())
        {
            this.items.add(new Item(this, itemCfg.getId()));
        }
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
