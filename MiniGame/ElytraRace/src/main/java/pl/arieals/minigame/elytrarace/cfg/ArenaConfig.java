package pl.arieals.minigame.elytrarace.cfg;

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
public class ArenaConfig
{
    @XmlElementWrapper(name = "startLocations")
    @XmlElement(name = "location")
    private List<XmlLocation> startLocations;
    @XmlElement(name = "restartLocation")
    private XmlLocation       restartLocation;
    @XmlElementWrapper(name = "checkpoints")
    @XmlElement(name = "checkpoint")
    private List<Checkpoint>  checkpoints;
    @XmlElementWrapper(name = "boosts")
    @XmlElement(name = "boost")
    private List<Boost>       boosts;
    @XmlElementWrapper(name = "scoreGroups")
    @XmlElement(name = "scoreGroup")
    private List<ScoreGroup>  scoreGroups;
    @XmlElementWrapper(name = "scores")
    @XmlElement(name = "score")
    private List<Score>       scores;
    @XmlElement(name = "metaRegion")
    private XmlCuboid         metaRegion;

    public ArenaConfig()
    {
    }

    public ArenaConfig(final List<XmlLocation> startLocations, final List<Checkpoint> checkpoints)
    {
        this.startLocations = startLocations;
        this.checkpoints = checkpoints;
    }

    public List<XmlLocation> getStartLocations()
    {
        return this.startLocations;
    }

    public XmlLocation getRestartLocation()
    {
        return this.restartLocation;
    }

    public List<Checkpoint> getCheckpoints()
    {
        return this.checkpoints;
    }

    public List<Boost> getBoosts()
    {
        return this.boosts;
    }

    public List<ScoreGroup> getScoreGroups()
    {
        return this.scoreGroups;
    }

    public List<Score> getScores()
    {
        return this.scores;
    }

    public XmlCuboid getMetaRegion()
    {
        return this.metaRegion;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("startLocations", this.startLocations).append("restartLocation", this.restartLocation).append("checkpoints", this.checkpoints).append("boosts", this.boosts).append("scoreGroups", this.scoreGroups).append("scores", this.scores).append("metaRegion", this.metaRegion).toString();
    }
}
