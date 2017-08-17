package pl.arieals.globalshops.controller.cfg;

import static pl.arieals.globalshops.shared.Rarity.NORMAL;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

import pl.arieals.globalshops.shared.Rarity;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
public final class ItemCfg
{
    @XmlElement(name = "id", required = true)
    private String            id;
    @XmlElement(name = "maxLevel")
    private Integer           maxLevel = 1; // default max level is 1, so item doesn't support leveling
    @XmlElement(name = "rarity")
    private Rarity            rarity = NORMAL; // default is normal
    @XmlElement(name = "name")
    private List<ItemName>    names = new ArrayList<>(0);
    @XmlElement(name = "itemData")
    private List<ItemDataCfg> itemData = new ArrayList<>(0);

    public String getId()
    {
        return this.id;
    }

    public Integer getMaxLevel()
    {
        return this.maxLevel;
    }

    public Rarity getRarity()
    {
        return this.rarity;
    }

    public List<ItemName> getNames()
    {
        return this.names;
    }

    public List<ItemDataCfg> getItemData()
    {
        return this.itemData;
    }
}
