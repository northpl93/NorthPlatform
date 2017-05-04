package pl.arieals.api.minigame.server.gamehost.utils.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.bukkit.Location;
import org.bukkit.World;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "location")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlLocation
{
    @XmlAttribute(required = true)
    private double x;
    @XmlAttribute(required = true)
    private double y;
    @XmlAttribute(required = true)
    private double z;
    @XmlAttribute
    private float  yaw;
    @XmlAttribute
    private float  pitch;

    public XmlLocation()
    {
    }

    public XmlLocation(final double x, final double y, final double z, final float yaw, final float pitch)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public double getX()
    {
        return this.x;
    }

    public double getY()
    {
        return this.y;
    }

    public double getZ()
    {
        return this.z;
    }

    public float getYaw()
    {
        return this.yaw;
    }

    public float getPitch()
    {
        return this.pitch;
    }

    public Location toBukkit(final World world)
    {
        return new Location(world, this.x, this.y, this.z, this.yaw, this.pitch);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("x", this.x).append("y", this.y).append("z", this.z).append("yaw", this.yaw).append("pitch", this.pitch).toString();
    }
}
