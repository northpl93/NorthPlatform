package pl.arieals.api.minigame.shared.api;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.utils.xml.XmlCuboid;

@XmlRootElement(name = "gameMap")
@XmlAccessorType(XmlAccessType.FIELD)
public class GameMapConfig
{
    @XmlElement
    private String              displayName;
    @XmlElement
    private boolean             enabled = true;
    @XmlElement
    private XmlCuboid           arenaRegion;
    @XmlElement
    private Map<String, String> properties = new HashMap<>();
    
    public String getDisplayName()
    {
        return this.displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public boolean isEnabled()
    {
        return enabled;
    }
    
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
    
    public XmlCuboid getArenaRegion()
    {
        return arenaRegion;
    }
    
    public void setArenaRegion(XmlCuboid arenaRegion)
    {
        this.arenaRegion = arenaRegion;
    }

    public Map<String, String> getProperties()
    {
        return this.properties;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("displayName", this.displayName).append("arenaRegion", this.arenaRegion).append("properties", this.properties).toString();
    }
}
