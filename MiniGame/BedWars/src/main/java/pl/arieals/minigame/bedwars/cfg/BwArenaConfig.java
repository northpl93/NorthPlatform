package pl.arieals.minigame.bedwars.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.utils.xml.XmlCuboid;
import pl.north93.zgame.api.bukkit.utils.xml.XmlLocation;

@XmlRootElement(name = "arena")
@XmlAccessorType(XmlAccessType.FIELD)
public class BwArenaConfig
{
    @XmlElement(name = "lobby", required = true)
    private XmlLocation        lobby;
    @XmlElement(name = "lobbyRegion", required = true)
    private XmlCuboid          lobbyCuboid;
    @XmlElementWrapper(name = "teams")
    @XmlElement(name = "team")
    private List<BwTeamConfig> teams;
    @XmlElementWrapper(name = "generators")
    @XmlElement(name = "generator")
    private List<BwGenerator>  generators;
    @XmlElementWrapper(name = "secureRegions")
    @XmlElement(name = "secureRegion")
    private List<XmlCuboid>    secureRegions;

    public XmlLocation getLobby()
    {
        return this.lobby;
    }

    public XmlCuboid getLobbyCuboid()
    {
        return this.lobbyCuboid;
    }

    public List<BwTeamConfig> getTeams()
    {
        return this.teams;
    }

    public List<BwGenerator> getGenerators()
    {
        return this.generators;
    }

    public List<XmlCuboid> getSecureRegions()
    {
        return this.secureRegions;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("lobby", this.lobby).append("lobbyCuboid", this.lobbyCuboid).append("teams", this.teams).append("generators", this.generators).append("secureRegions", this.secureRegions).toString();
    }
}
