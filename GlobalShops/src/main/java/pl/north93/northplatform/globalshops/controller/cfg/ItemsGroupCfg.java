package pl.north93.northplatform.globalshops.controller.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.globalshops.shared.GroupType;
import pl.north93.northplatform.api.global.serializer.platform.annotations.NorthField;

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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("id", this.id).append("groupType", this.groupType).append("items", this.items).toString();
    }
}
