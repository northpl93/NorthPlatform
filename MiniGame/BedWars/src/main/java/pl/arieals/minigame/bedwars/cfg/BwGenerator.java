package pl.arieals.minigame.bedwars.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.utils.xml.XmlLocation;

@XmlRootElement(name = "generator")
@XmlAccessorType(XmlAccessType.FIELD)
public class BwGenerator
{
    @XmlElement(required = true)
    private String      type;
    @XmlElement(required = true)
    private XmlLocation location;

    public BwGenerator()
    {
    }

    public BwGenerator(final String type, final XmlLocation location)
    {
        this.type = type;
        this.location = location;
    }

    public String getType()
    {
        return this.type;
    }

    public XmlLocation getLocation()
    {
        return this.location;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("type", this.type).append("location", this.location).toString();
    }
}
