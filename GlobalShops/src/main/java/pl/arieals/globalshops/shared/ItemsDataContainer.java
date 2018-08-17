package pl.arieals.globalshops.shared;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.globalshops.controller.cfg.ItemsGroupCfg;
import pl.north93.zgame.api.global.serializer.platform.annotations.NorthField;

/**
 * Klasa uzywana do wymieniania danych przez system configow.
 */
public class ItemsDataContainer
{
    @NorthField(type = ArrayList.class)
    private List<ItemsGroupCfg> groups;

    public ItemsDataContainer()
    {
    }

    public ItemsDataContainer(final List<ItemsGroupCfg> groups)
    {
        this.groups = groups;
    }

    public List<ItemsGroupCfg> getGroups()
    {
        return this.groups;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("groups", this.groups).toString();
    }
}
