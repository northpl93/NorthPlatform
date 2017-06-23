package pl.arieals.minigame.bedwars.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "generatorType")
@XmlAccessorType(XmlAccessType.FIELD)
public class BedWarsGeneratorType
{
    @XmlElement(required = true)
    private String                           name;
    @XmlElement(required = true)
    private int                              overload;
    @XmlElementWrapper(name = "items", required = true)
    @XmlElement(name = "generatorItem")
    private List<BedWarsGeneratorItemConfig> items;

    public BedWarsGeneratorType()
    {
    }

    public BedWarsGeneratorType(final String name, final int overload, final List<BedWarsGeneratorItemConfig> items)
    {
        this.name = name;
        this.overload = overload;
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

    public List<BedWarsGeneratorItemConfig> getItems()
    {
        return this.items;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("overload", this.overload).append("items", this.items).toString();
    }
}
