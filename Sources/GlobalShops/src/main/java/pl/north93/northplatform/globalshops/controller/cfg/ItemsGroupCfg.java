package pl.north93.northplatform.globalshops.controller.cfg;

import lombok.ToString;
import pl.north93.northplatform.globalshops.shared.GroupType;
import pl.north93.serializer.platform.annotations.NorthField;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@ToString
@XmlRootElement(name = "itemGroup")
@XmlAccessorType(XmlAccessType.FIELD)
public final class ItemsGroupCfg
{
    @XmlElement
    private String        id;
    @XmlElement
    private GroupType     groupType;
    @XmlElementWrapper(name = "items")
    @XmlElement(name = "item")
    @NorthField(type = ArrayList.class)
    private List<ItemCfg> items;

    public String getId()
    {
        return this.id;
    }

    public GroupType getGroupType()
    {
        return this.groupType;
    }

    public List<ItemCfg> getItems()
    {
        return this.items;
    }
}
