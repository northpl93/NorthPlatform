package pl.arieals.minigame.elytrarace.cfg;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import pl.north93.zgame.api.bukkit.utils.xml.XmlLocation;


@XmlRootElement(name = "arena")
@XmlAccessorType(XmlAccessType.FIELD)
public class ArenaConfig
{
    @XmlElementWrapper(name = "startLocations")
    @XmlElement(name = "location")
    private List<XmlLocation> startLocations;
    @XmlElementWrapper(name = "checkpoints")
    @XmlElement(name = "checkpoint")
    private List<Checkpoint> checkpoints;

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
}
