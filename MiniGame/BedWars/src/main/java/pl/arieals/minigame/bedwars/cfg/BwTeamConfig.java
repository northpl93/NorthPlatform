package pl.arieals.minigame.bedwars.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.bukkit.ChatColor;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.utils.xml.XmlCuboid;
import pl.north93.zgame.api.bukkit.utils.xml.XmlLocation;

@XmlRootElement(name = "team")
@XmlAccessorType(XmlAccessType.FIELD)
public class BwTeamConfig
{
    @XmlAttribute(required = true)
    private int         scoreboardOrder;
    @XmlAttribute(required = true)
    private ChatColor   color;
    @XmlElement(required = true, name = "teamRegion")
    private XmlCuboid   teamRegion;
    @XmlElement(required = true, name = "spawnLocation")
    private XmlLocation spawnLocation;
    @XmlElement(required = true, name = "bedLocation")
    private XmlLocation bedLocation;

    public int getScoreboardOrder()
    {
        return this.scoreboardOrder;
    }

    public ChatColor getColor()
    {
        return this.color;
    }

    public XmlCuboid getTeamRegion()
    {
        return this.teamRegion;
    }

    public XmlLocation getSpawnLocation()
    {
        return this.spawnLocation;
    }

    public XmlLocation getBedLocation()
    {
        return this.bedLocation;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("scoreboardOrder", this.scoreboardOrder).append("color", this.color).append("teamRegion", this.teamRegion).append("spawnLocation", this.spawnLocation).append("bedLocation", this.bedLocation).toString();
    }
}
