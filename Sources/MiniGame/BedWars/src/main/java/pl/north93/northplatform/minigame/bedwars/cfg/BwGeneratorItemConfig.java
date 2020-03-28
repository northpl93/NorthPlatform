package pl.north93.northplatform.minigame.bedwars.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.bukkit.Material;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "generatorItem")
@XmlAccessorType(XmlAccessType.FIELD)
public class BwGeneratorItemConfig
{
    @XmlAttribute
    private String   name;
    @XmlAttribute(required = true)
    private Material material;
    @XmlAttribute
    private byte     data = 0;
    @XmlAttribute
    private int      amount = 1;
    @XmlAttribute(required = true)
    private int      every;
    @XmlAttribute()
    private boolean  triggerable;
    @XmlAttribute
    private int      startAt = 0;

    public BwGeneratorItemConfig()
    {
    }

    public BwGeneratorItemConfig(final String name, final Material material, final byte data, final int amount, final int every, final boolean triggerable, final int startAt)
    {
        this.name = name;
        this.material = material;
        this.data = data;
        this.amount = amount;
        this.every = every;
        this.triggerable = triggerable;
        this.startAt = startAt;
    }

    public String getName()
    {
        return this.name;
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

    public boolean isTriggerable()
    {
        return this.triggerable;
    }

    public int getStartAt()
    {
        return this.startAt;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("material", this.material).append("data", this.data).append("amount", this.amount).append("every", this.every).append("triggerable", this.triggerable).append("startAt", this.startAt).toString();
    }
}
