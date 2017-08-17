package pl.arieals.globalshops.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.globalshops.controller.cfg.ItemCfg;
import pl.arieals.globalshops.controller.cfg.ItemDataCfg;
import pl.arieals.globalshops.controller.cfg.ItemName;
import pl.arieals.globalshops.controller.cfg.ItemsGroupCfg;

/**
 * Przedstawia grupe przedmiotow.
 */
public final class ItemsGroup
{
    private final String     id;
    private final GroupType  groupType;
    private final List<Item> items;

    public ItemsGroup(final ItemsGroupCfg cfg)
    {
        this.id = cfg.getId();
        this.groupType = cfg.getGroupType();
        this.items = new ArrayList<>();
        for (final ItemCfg itemCfg : cfg.getItems())
        {
            final Map<Locale, String> name = itemCfg.getNames().stream().collect(Collectors.toMap(itemName -> Locale.forLanguageTag(itemName.getLang()), ItemName::getName));
            final Map<String, String> itemData = itemCfg.getItemData().stream().collect(Collectors.toMap(ItemDataCfg::getName, ItemDataCfg::getValue));
            this.items.add(new Item(this, itemCfg.getId(), itemCfg.getMaxLevel(), itemCfg.getRarity(), name, itemData));
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
