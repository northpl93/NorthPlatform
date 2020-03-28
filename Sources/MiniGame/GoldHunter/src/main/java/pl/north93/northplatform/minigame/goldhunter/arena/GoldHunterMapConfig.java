package pl.north93.northplatform.minigame.goldhunter.arena;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Preconditions;

import pl.north93.northplatform.api.bukkit.utils.xml.XmlLocation;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "goldHunterMap")
public class GoldHunterMapConfig
{
    @XmlElement(required = true)
    private XmlLocation spawnRed;
    @XmlElement(required = true)
    private XmlLocation spawnBlue;
    
    @XmlElementWrapper
    @XmlElement(name = "chest")
    private List<XmlLocation> chestsRed = new ArrayList<>();
    
    @XmlElementWrapper
    @XmlElement(name = "chest")
    private List<XmlLocation> chestsBlue = new ArrayList<>();
    
    private int buildLimit = 255;
    
    public XmlLocation getSpawn1()
    {
        return spawnRed;
    }
    
    public void setSpawn1(XmlLocation spawn1)
    {
        this.spawnRed = spawn1;
    }
    
    public XmlLocation getSpawn2()
    {
        return spawnBlue;
    }
    
    public void setSpawn2(XmlLocation spawn2)
    {
        this.spawnBlue = spawn2;
    }
    
    public List<XmlLocation> getChestsRed()
    {
        return chestsRed;
    }
    
    public List<XmlLocation> getChestsBlue()
    {
        return chestsBlue;
    }
    
    public int getBuildLimit()
    {
        return buildLimit;
    }
    
    public void validateConfig() throws IllegalArgumentException
    {
        Preconditions.checkArgument(chestsRed.size() == chestsBlue.size(), "Both teams must have the same number of chests");
        Preconditions.checkArgument(chestsBlue.size() > 0, "Each team must have at least one chest");
        Preconditions.checkArgument(buildLimit > 0 && buildLimit <= 255, "Build limit must be between 0 and 255");
    }
}
