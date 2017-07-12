package pl.arieals.minigame.goldhunter;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Preconditions;

import pl.north93.zgame.api.bukkit.utils.xml.XmlLocation;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "goldHunterMap")
public class GoldHunterMapConfig
{
    @XmlElement(required = true)
    private XmlLocation spawn1;
    @XmlElement(required = true)
    private XmlLocation spawn2;
    
    @XmlElementWrapper(name = "chests1", required = true)
    @XmlElement(name = "chest")
    private List<XmlLocation> chests1 = new ArrayList<>();
    
    @XmlElementWrapper(name = "chests2", required = true)
    @XmlElement(name = "chest")
    private List<XmlLocation> chests2 = new ArrayList<>();
    
    public XmlLocation getSpawn1()
    {
        return spawn1;
    }
    
    public void setSpawn1(XmlLocation spawn1)
    {
        this.spawn1 = spawn1;
    }
    
    public XmlLocation getSpawn2()
    {
        return spawn2;
    }
    
    public void setSpawn2(XmlLocation spawn2)
    {
        this.spawn2 = spawn2;
    }
    
    public List<XmlLocation> getChests1()
    {
        return chests1;
    }
    
    public List<XmlLocation> getChests2()
    {
        return chests2;
    }
    
    public void validateConfig() throws IllegalArgumentException
    {
        Preconditions.checkArgument(chests1.size() == chests2.size(), "Both teams must have the same number of chests");
        Preconditions.checkArgument(chests1.size() > 0, "Each team must have at least one chest");
    }
}
