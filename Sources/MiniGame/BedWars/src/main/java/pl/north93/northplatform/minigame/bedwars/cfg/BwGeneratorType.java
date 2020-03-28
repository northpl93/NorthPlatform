package pl.north93.northplatform.minigame.bedwars.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;

import org.bukkit.Material;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "generatorType")
@XmlAccessorType(XmlAccessType.FIELD)
public class BwGeneratorType
{
    @XmlElement(required = true)
    private String                      name;
    @XmlElement(required = true)
    private int                         overload;
    @XmlElement(required = true)
    private boolean                     randomLocation;
    @XmlElement
    private Material                    hudItem; // itemek wyswietlajacy sie nad generatorem
    @XmlElementWrapper(name = "items", required = true)
    @XmlElement(name = "generatorItem")
    private List<BwGeneratorItemConfig> items;

    public BwGeneratorType()
    {
    }

    public BwGeneratorType(final String name, final int overload, final Material hudItem, final List<BwGeneratorItemConfig> items)
    {
        this.name = name;
        this.overload = overload;
        this.hudItem = hudItem;
        this.items = items;
    }

    public String getName()
    {
        return this.name;
    }

    public int getOverload()
    {
        return this.overload;
    }

    public boolean isRandomLocation()
    {
        return this.randomLocation;
    }

    public Material getHudItem()
    {
        return this.hudItem;
    }

    public List<BwGeneratorItemConfig> getItems()
    {
        return this.items;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("overload", this.overload).append("randomLocation", this.randomLocation).append("hudItem", this.hudItem).append("items", this.items).toString();
    }
}
