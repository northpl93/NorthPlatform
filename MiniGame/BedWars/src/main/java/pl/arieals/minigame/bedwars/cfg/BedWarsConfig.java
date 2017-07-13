package pl.arieals.minigame.bedwars.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.Map;

/**
 * Globalna konfiguracja rozgrywki BedWars.
 */
@XmlRootElement(name = "bedwars")
@XmlAccessorType(XmlAccessType.FIELD)
public class BedWarsConfig
{
    @XmlElement
    private Integer              destroyBedsAt;
    @XmlElement
    private Integer              startDeathMatchAt;
    @XmlElement
    private Integer              teamSize;
    @XmlElement
    private Map<String, Integer> upgrades;

    public Integer getDestroyBedsAt()
    {
        return this.destroyBedsAt;
    }

    public Integer getStartDeathMatchAt()
    {
        return this.startDeathMatchAt;
    }

    public Integer getTeamSize()
    {
        return this.teamSize;
    }

    public Map<String, Integer> getUpgrades()
    {
        return this.upgrades;
    }
}
