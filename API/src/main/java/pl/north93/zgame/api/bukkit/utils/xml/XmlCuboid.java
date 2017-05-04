package pl.north93.zgame.api.bukkit.utils.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.bukkit.World;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.shared.utils.Cuboid;

@XmlRootElement(name = "cuboid")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlCuboid
{
    @XmlElement(required = true)
    private XmlLocation from;
    @XmlElement(required = true)
    private XmlLocation to;

    public XmlCuboid()
    {
    }

    public XmlCuboid(final XmlLocation from, final XmlLocation to)
    {
        this.from = from;
        this.to = to;
    }

    public XmlLocation getFrom()
    {
        return this.from;
    }

    public XmlLocation getTo()
    {
        return this.to;
    }

    public Cuboid toCuboid(final World world)
    {
        return new Cuboid(this.from.toBukkit(world), this.to.toBukkit(world));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("from", this.from).append("to", this.to).toString();
    }
}
