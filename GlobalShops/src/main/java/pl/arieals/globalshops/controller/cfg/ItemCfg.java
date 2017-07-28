package pl.arieals.globalshops.controller.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
public final class ItemCfg
{
    @XmlElement(name = "id", required = true)
    private String            id;
    @XmlElement(name = "itemData")
    private List<ItemDataCfg> itemData;

    public String getId()
    {
        return this.id;
    }

    public List<ItemDataCfg> getItemData()
    {
        return this.itemData;
    }
}
