package pl.mcpiraci.world.properties.impl;

import javax.xml.bind.JAXB;

import java.io.File;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;

import lombok.extern.slf4j.Slf4j;
import pl.mcpiraci.world.properties.IWorldProperties;
import pl.mcpiraci.world.properties.PropertiesConfig;
import pl.mcpiraci.world.properties.Weather;
import pl.mcpiraci.world.properties.impl.util.GamerulesUtils;
import pl.mcpiraci.world.properties.impl.xml.XmlWorldProperties;
import pl.north93.zgame.api.bukkit.tick.ITickable;
import pl.north93.zgame.api.bukkit.tick.Tick;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

@Slf4j
public class WorldPropertiesImpl implements IWorldProperties, ITickable
{
    @Inject
    private static PropertiesManagerImpl propertiesManager;
    
    private final World world;
    
    private final PropertiesConfig worldConfig;
    private long lastUpdated;
    
    public WorldPropertiesImpl(World world)
    {
        this.world = world;
        
        worldConfig = new PropertiesConfig(propertiesManager.getServerConfig());
        lastUpdated = worldConfig.getModifiedTimestamp();
    }

    @Override
    public World getWorld()
    {
        return world;
    }

    @Override
    public PropertiesConfig getWorldConfig()
    {
        return worldConfig;
    }
    
    @Override
    public boolean isBuildAllowed()
    {
        return worldConfig.canBuildValue();
    }

    @Override
    public boolean isInterractAllowed()
    {
        return worldConfig.canInteractValue();
    }

    @Override
    public boolean arePlayersInvulnerable()
    {
       return worldConfig.playersInvulnerableValue();
    }
    
    @Override
    public boolean isPhysicsEnabled()
    {
        return worldConfig.physicsEnabledValue();
    }

    @Override
    public boolean isHungerEnabled()
    {
        return worldConfig.hungerEnabledValue();
    }
    
    @Override
    public boolean isMobSpawningEnabled()
    {
        return worldConfig.mobSpawningValue();
    }
    
    @Override
    public int getTime()
    {
        return worldConfig.timeValue();
    }

    @Override
    public Weather getWeather()
    {
        return worldConfig.weatherValue();
    }

    @Override
    public Map<String, String> getGamerules()
    {
        return ImmutableMap.copyOf(worldConfig.gamerulesValue());
    }

    @Override
    public GameMode getGamemode()
    {
        return worldConfig.gamemodeValue();
    }

    @Override
    public Location getSpawn()
    {
        return worldConfig.spawnValue();
    }

    @Override
    public void reloadWorldConfig()
    {
        worldConfig.setDefaultValues();
        
        try
        {
            File xmlFile = new File(world.getWorldFolder(), "world-properties.xml");
            if ( xmlFile.isFile() )
            {
                JAXB.unmarshal(xmlFile, XmlWorldProperties.class).applyToConfig(worldConfig);
            }
            else
            {
                log.warn("world-properties.xml for world {} doesn't exist, using default properties", world.getName());
            }
            
            worldConfig.toString();
            log.info("Reloaded world-properties.xml for world {}", world.getName());
            log.debug("{} world-properties config: {}", world.getName(), worldConfig);
        }
        catch ( Throwable e )
        {
            log.error("An error occured while reloading world-properties.xml for world {}. Using default properties", world.getName(), e);
        }
    }
    
    @Tick
    private void handleConfigUpdate()
    {
        long timestamp = worldConfig.getModifiedTimestamp();
        if ( timestamp != lastUpdated )
        {
            lastUpdated = timestamp;
            updateWorld();
        }
    }
    
    public void updateWorld()
    {
        Preconditions.checkState(worldConfig != null);
        // updateGamerules() method must be first called, because updateWorldWeather() and updateWorldTime() add their owns gamerules
        updateGamerules(); 
        updateWorldTime();
        updateWorldWeather();
        updateSpawn();
    }
    
    private void updateGamerules()
    {
        Map<String, String> gamerules = worldConfig.gamerulesValue();
        
        // Make sure we don't ovveride gamerules about weather and time
        gamerules.remove("doDaylightCycle");
        gamerules.remove("doWeatherCycle");
        
        GamerulesUtils.resetGamerules(world);
        gamerules.entrySet().forEach(e -> world.setGameRuleValue(e.getKey(), e.getValue()));

        if (log.isDebugEnabled())
        {
            final Map<String, String> gameruleValues = GamerulesUtils.getGameruleValues(world);
            log.debug("Gamerules for world {} updated! Current values {}", world.getName(), gameruleValues);
        }
    }
    
    private void updateWorldWeather()
    {
        Weather weather = getWeather();
        
        if ( weather == null )
        {
            world.setGameRuleValue("doWeatherCycle", "true");
            // XXX: shall we reset wether duration?
        }
        else
        {
            world.setGameRuleValue("doWeatherCycle", "false");
            world.setStorm(weather.hasRain());
            world.setThundering(weather.hasThundering());
        }
        
        log.debug("Weather for world {} updated!", world.getName());
    }
    
    private void updateWorldTime()
    {
        int time = getTime();
        
        if ( time < 0 )
        {
            world.setGameRuleValue("doDaylightCycle", "true");
        }
        else
        {
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setTime(time);
        }
        
        log.debug("Time for world {} updated!");
    }

    private void updateSpawn()
    {
        final Location location = getSpawn();

        if (location == null)
        {
            return;
        }

        world.setSpawnLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        log.debug("Spawn location for world {} updated!");
    }
}
