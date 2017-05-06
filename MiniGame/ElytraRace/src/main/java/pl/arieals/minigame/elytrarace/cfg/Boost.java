package pl.arieals.minigame.elytrarace.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.minigame.elytrarace.BoostType;
import pl.north93.zgame.api.bukkit.utils.xml.XmlCuboid;

@XmlRootElement(name = "boost")
@XmlAccessorType(XmlAccessType.FIELD)
public class Boost
{
    @XmlElement(required = true)
    private XmlCuboid area;
    @XmlElement(required = true)
    private BoostType boostType;
    @XmlElement(required = true)
    private Integer   boostPower;

    public Boost()
    {
    }

    public Boost(final XmlCuboid area, final BoostType boostType)
    {
        this.area = area;
        this.boostType = boostType;
    }

    public XmlCuboid getArea()
    {
        return this.area;
    }

    public BoostType getBoostType()
    {
        return this.boostType;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("area", this.area).append("boostType", this.boostType).toString();
    }
}
