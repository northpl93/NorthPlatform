package pl.arieals.minigame.bedwars.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlAccessorType(XmlAccessType.NONE)
public class BwRewardsConfig
{
    @XmlElement
    private int kill;
    @XmlElement
    private int elimination;
    @XmlElement
    private int bedDestroy;
    @XmlElement
    private int win;

    public int getKill()
    {
        return this.kill;
    }

    public int getElimination()
    {
        return this.elimination;
    }

    public int getBedDestroy()
    {
        return this.bedDestroy;
    }

    public int getWin()
    {
        return this.win;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("kill", this.kill).append("elimination", this.elimination).append("bedDestroy", this.bedDestroy).append("win", this.win).toString();
    }
}
