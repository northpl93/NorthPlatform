package pl.arieals.minigame.elytrarace.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "scoreGroup")
@XmlAccessorType(XmlAccessType.FIELD)
public class ScoreGroup
{
    @XmlAttribute(required = true)
    private String  name;
    @XmlAttribute(required = true)
    private Integer points;
    @XmlAttribute(required = true)
    private Integer comboPoints;

    public ScoreGroup()
    {
    }

    public ScoreGroup(final String name, final Integer points, final Integer comboPoints)
    {
        this.name = name;
        this.points = points;
        this.comboPoints = comboPoints;
    }

    public String getName()
    {
        return this.name;
    }

    public Integer getPoints()
    {
        return this.points;
    }

    public Integer getComboPoints()
    {
        return this.comboPoints;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("points", this.points).append("comboPoints", this.comboPoints).toString();
    }
}
