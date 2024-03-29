package pl.north93.northplatform.minigame.elytrarace.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.utils.xml.XmlCuboid;

@XmlRootElement(name = "score_point")
@XmlAccessorType(XmlAccessType.FIELD)
public class Score
{
    @XmlElement(required = true)
    private XmlCuboid area;
    @XmlAttribute()
    private String    achieveGroup;
    private String    scoreGroup;

    public Score()
    {
    }

    public Score(final XmlCuboid area, final String achieveGroup, final String scoreGroup)
    {
        this.area = area;
        this.achieveGroup = achieveGroup;
        this.scoreGroup = scoreGroup;
    }

    public XmlCuboid getArea()
    {
        return this.area;
    }

    public String getAchieveGroup()
    {
        return this.achieveGroup;
    }

    public String getScoreGroup()
    {
        return this.scoreGroup;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("area", this.area).append("scoreGroup", this.scoreGroup).toString();
    }
}
