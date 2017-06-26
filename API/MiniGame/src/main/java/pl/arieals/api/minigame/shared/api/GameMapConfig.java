package pl.arieals.api.minigame.shared.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.utils.xml.XmlChunk;

@XmlRootElement(name = "gameMap")
@XmlAccessorType(XmlAccessType.FIELD)
public class GameMapConfig
{
    @XmlElement(required = true)
    private String              displayName;
    @XmlElement
    private boolean             enabled = true;
    @XmlElement
    private Map<String, String> properties = new HashMap<>();
    @XmlElement
    private Map<String, String> gameRules = new HashMap<>();
    @XmlElementWrapper(name = "chunks", required = true)
    @XmlElement(name = "chunk")
    private Set<XmlChunk>       chunks = new HashSet<>();
    
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
        return this.enabled;
    }
    
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public Set<XmlChunk> getChunks()
    {
        return this.chunks;
    }

    public Map<String, String> getProperties()
    {
        return this.properties;
    }

    public Map<String, String> getGameRules()
    {
        return this.gameRules;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("displayName", this.displayName).append("enabled", this.enabled).append("chunks", this.chunks).append("properties", this.properties).append("gameRules", this.gameRules).toString();
    }
}
