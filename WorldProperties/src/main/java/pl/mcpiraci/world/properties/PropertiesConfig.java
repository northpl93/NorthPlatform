package pl.mcpiraci.world.properties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;

import com.google.common.collect.ImmutableMap;

import org.bukkit.GameMode;
import org.bukkit.Location;

import org.apache.commons.lang3.builder.ToStringBuilder;

public final class PropertiesConfig
{
    private final PropertiesConfig parent;
    private final Set<PropertiesConfig> children = Collections.newSetFromMap(new WeakHashMap<>());
    
    private long modifiedTimestamp;
    
    private Boolean canBuild;
    private Boolean canInteract;
    private Boolean playersInvulnerable;
    private Integer time;
    private Weather weather;
    private Boolean physicsEnabled;
    private GameMode gamemode;
    private Location spawn;
    
    private Map<String, String> gamerules = Collections.emptyMap();
    
    public PropertiesConfig(PropertiesConfig parent)
    {
        this.parent = parent;
        
        Optional.ofNullable(parent).ifPresent(p -> p.children.add(this));
        updateModifiedTimestamp();
    }
    
    public long getModifiedTimestamp()
    {
        return modifiedTimestamp;
    }
    
    private void updateModifiedTimestamp()
    {
        modifiedTimestamp = System.currentTimeMillis();
        
        // update modified timestamp in children also to detect changes in parent
        children.forEach(PropertiesConfig::updateModifiedTimestamp);
    }
    
    public boolean canBuildValue()
    {
        if ( getCanBuild() != null )
        {
            return getCanBuild().booleanValue();
        }
        
        return parent != null ? parent.canBuildValue() : DefaultPlayerProperties.CAN_BUILD;
    }
    
    public boolean canInteractValue()
    {
        if ( getCanInteract() != null )
        {
            return getCanInteract().booleanValue();
        }
        
        return parent != null ? parent.canInteractValue() : DefaultPlayerProperties.CAN_INTERRACT;
    }
    
    public boolean playersInvulnerableValue()
    {
        if ( getPlayersInvulnerable() != null )
        {
            return getPlayersInvulnerable().booleanValue();
        }
        
        return parent != null ? parent.playersInvulnerableValue() : DefaultPlayerProperties.PLAYERS_INVULNERABLE;
    }
    
    public int timeValue()
    {
        if ( getTime() != null )
        {
            return getTime().intValue();
        }
        
        return parent != null ? parent.timeValue() : -1;
    }
    
    public Weather weatherValue()
    {
        if ( getWeather() != null )
        {
            return weather;
        }
        
        return parent != null ? parent.weatherValue() : null;
    }
    
    public boolean physicsEnabledValue()
    {
        if ( getPhysicsEnabled() != null )
        {
            return physicsEnabled.booleanValue();
        }
        
        return parent != null ? parent.physicsEnabledValue() : true;
    }
    
    public Map<String, String> gamerulesValue()
    {
        Map<String, String> parentGamerules = parent != null ? parent.gamerulesValue() : Collections.emptyMap();
        
        Map<String, String> result = new HashMap<>();
        result.putAll(parentGamerules);
        
        for ( Entry<String, String> entry : gamerules.entrySet() )
        {
            if ( entry.getValue() == null || entry.getValue().isEmpty() )
            {
                result.remove(entry.getKey());
            }
            else
            {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        
        return result;
    }
    
    public GameMode gamemodeValue()
    {
        if ( gamemode != null )
        {
            return gamemode;
        }
        
        return parent != null ? parent.getGamemode() : null;
    }

    public Location spawnValue()
    {
        if ( spawn != null )
        {
            return spawn;
        }

        return parent != null ? parent.getSpawn() : null;
    }

    public Boolean getCanBuild()
    {
        return canBuild;
    }
    
    public Boolean getCanInteract()
    {
        return canInteract;
    }
    
    public Boolean getPlayersInvulnerable()
    {
        return playersInvulnerable;
    }
    
    public Integer getTime()
    {
        return time;
    }
    
    public Weather getWeather()
    {
        return weather;
    }
    
    public Boolean getPhysicsEnabled()
    {
        return physicsEnabled;
    }
    
    public Map<String, String> getGamerules()
    {
        return gamerules;
    }
    
    public GameMode getGamemode()
    {
        return gamemode;
    }

    public Location getSpawn()
    {
        return this.spawn;
    }

    public void setCanBuild(Boolean canBuild)
    {
        this.canBuild = canBuild;
        updateModifiedTimestamp();
    }
    
    public void setCanInteract(Boolean canInteract)
    {
        this.canInteract = canInteract;
        updateModifiedTimestamp();
    }
    
    public void setPlayersInvulnerable(Boolean playersInvulnerable)
    {
        this.playersInvulnerable = playersInvulnerable;
        updateModifiedTimestamp();
    }
    
    public void setTime(Integer time)
    {
        this.time = time;
        updateModifiedTimestamp();
    }
    
    public void setWeather(Weather weather)
    {
        this.weather = weather;
        updateModifiedTimestamp();
    }
    
    public void setPhysicsEnabled(Boolean flag)
    {
        this.physicsEnabled = flag;
        updateModifiedTimestamp();
    }
    
    public void setGamerules(Map<String, String> gamerules)
    {
        this.gamerules = ImmutableMap.copyOf(gamerules);
        updateModifiedTimestamp();
    }
    
    public void setGamemode(GameMode gamemode)
    {
        this.gamemode = gamemode;
        updateModifiedTimestamp();
    }

    public void setSpawn(Location spawn)
    {
        this.spawn = spawn;
        updateModifiedTimestamp();
    }

    public void setDefaultValues()
    {
        canBuild = null;
        canInteract = null;
        playersInvulnerable = null;
        time = null;
        weather = null;
        gamerules = Collections.emptyMap();
        gamemode = null;
        spawn = null;
        
        updateModifiedTimestamp();
    }
    
    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("canBuild", canBuild)
                .append("canInteract", canInteract)
                .append("playersInvulnerable", playersInvulnerable)
                .append("time", time)
                .append("weather", weather)
                .append("gamerules", gamerules)
                .append("spawn", spawn)
                .append("effectiveCanBuild", canBuildValue())
                .append("effectiveCanInteract", canInteractValue())
                .append("effectivePlayersInvulnerable", playersInvulnerableValue())
                .append("effectiveTime", timeValue())
                .append("effectiveWeather", weatherValue())
                .append("effectiveGamerules", gamerulesValue())
                .append("effectiveSpawn", spawnValue())
                .build();
    }
}

