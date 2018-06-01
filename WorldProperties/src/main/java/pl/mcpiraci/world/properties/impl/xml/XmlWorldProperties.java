package pl.mcpiraci.world.properties.impl.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.bukkit.GameMode;

import com.google.common.base.Preconditions;

import pl.mcpiraci.world.properties.PropertiesConfig;
import pl.mcpiraci.world.properties.Weather;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "properties")
public class XmlWorldProperties
{
    private Weather weather;
    
    private Integer time;
    
    private XmlWorldProtection protection = new XmlWorldProtection();
    
    @XmlElement(name = "gamerule")
    private List<XmlGamerule> gamerules = new ArrayList<>();
    
    private GameMode gamemode;
    
    private Boolean physics;
    
    public Weather getWeather()
    {
        return weather;
    }
    
    public int getTime()
    {
        return time;
    }
    
    public Boolean getPhysics()
    {
        return physics;
    }
    
    public XmlWorldProtection getProtection()
    {
        return protection;
    }
    
    public List<XmlGamerule> getGamerules()
    {
        return gamerules;
    }
    
    public Map<String, String> getGamerulesAsMap()
    {
        return gamerules.stream().collect(Collectors.toMap(XmlGamerule::getName, XmlGamerule::getValue));
    }
    
    public GameMode getGamemode()
    {
        return gamemode;
    }
    
    public void applyToConfig(PropertiesConfig config)
    {
        Preconditions.checkArgument(config != null);
        
        config.setCanBuild(protection.isBuildAllowed());
        config.setCanInteract(protection.isInterractAllowed());
        config.setPlayersInvulnerable(protection.isPlayersInvulnerable());
        config.setWeather(weather);
        config.setTime(time);
        config.setGamerules(getGamerulesAsMap());
        config.setGamemode(gamemode);
    }
}
