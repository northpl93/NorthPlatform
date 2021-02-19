package pl.north93.northplatform.minigame.elytrarace.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.utils.xml.XmlCuboid;
import pl.north93.northplatform.api.bukkit.utils.xml.XmlLocation;


@XmlRootElement(name = "checkpoint")
@XmlAccessorType(XmlAccessType.FIELD)
public class Checkpoint
{
    @XmlAttribute(required = true)
    private int         number;
    private XmlCuboid   area;
    private XmlLocation teleport;

    public Checkpoint()
    {
    }

    public Checkpoint(final int number, final XmlCuboid area, final XmlLocation teleport)
    {
        this.number = number;
        this.area = area;
        this.teleport = teleport;
    }

    public int getNumber()
    {
        return this.number;
    }

    public XmlCuboid getArea()
    {
        return this.area;
    }

    public XmlLocation getTeleport()
    {
        return this.teleport;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("number", this.number).append("area", this.area).append("teleport", this.teleport).toString();
    }
}
