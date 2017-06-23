package pl.arieals.minigame.bedwars.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.bukkit.Material;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "generatorItem")
@XmlAccessorType(XmlAccessType.FIELD)
public class BedWarsGeneratorItemConfig
{
    @XmlAttribute(required = true)
    private Material material;
    @XmlAttribute
    private byte     data = 0;
    @XmlAttribute
    private int      amount = 1;
    @XmlAttribute(required = true)
    private int      every;
    @XmlAttribute
    private int      startAt = 0;

    public BedWarsGeneratorItemConfig()
    {
    }

    public BedWarsGeneratorItemConfig(final Material material, final byte data, final int amount, final int every, final int startAt)
    {
        this.material = material;
        this.data = data;
        this.amount = amount;
        this.every = every;
        this.startAt = startAt;
    }

    public Material getMaterial()
    {
        return this.material;
    }

    public byte getData()
    {
        return this.data;
    }

    public int getAmount()
    {
        return this.amount;
    }

    public int getEvery()
    {
        return this.every;
    }

    public int getStartAt()
    {
        return this.startAt;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("material", this.material).append("data", this.data).append("amount", this.amount).append("every", this.every).append("startAt", this.startAt).toString();
    }
}
