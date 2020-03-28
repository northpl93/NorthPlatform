package pl.north93.northplatform.minigame.elytrarace.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.minigame.elytrarace.BoostType;
import pl.north93.northplatform.api.bukkit.utils.xml.XmlCuboid;

@XmlRootElement(name = "boost")
@XmlAccessorType(XmlAccessType.FIELD)
public class Boost
{
    @XmlElement(required = true)
    private XmlCuboid area;
    @XmlElement(required = true)
    private BoostType boostType;
    @XmlElement
    private Double    heightPower;
    @XmlElement
    private Double    speedPower;

    public Boost()
    {
    }

    public Boost(final XmlCuboid area, final BoostType boostType, final Double heightPower, final Double speedPower)
    {
        this.area = area;
        this.boostType = boostType;
        this.heightPower = heightPower;
        this.speedPower = speedPower;
    }

    public XmlCuboid getArea()
    {
        return this.area;
    }

    public BoostType getBoostType()
    {
        return this.boostType;
    }

    public Double getHeightPower()
    {
        return this.heightPower;
    }

    public void setHeightPower(final Double heightPower)
    {
        this.heightPower = heightPower;
    }

    public Double getSpeedPower()
    {
        return this.speedPower;
    }

    public void setSpeedPower(final Double speedPower)
    {
        this.speedPower = speedPower;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("area", this.area).append("boostType", this.boostType).append("heightPower", this.heightPower).append("speedPower", this.speedPower).toString();
    }
}
