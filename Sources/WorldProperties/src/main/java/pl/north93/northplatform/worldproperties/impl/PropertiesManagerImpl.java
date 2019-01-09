package pl.north93.northplatform.worldproperties.impl;

import javax.xml.bind.JAXB;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.Preconditions;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.worldproperties.IPlayerProperties;
import pl.north93.northplatform.worldproperties.IWorldProperties;
import pl.north93.northplatform.worldproperties.IWorldPropertiesManager;
import pl.north93.northplatform.worldproperties.PropertiesConfig;
import pl.north93.northplatform.worldproperties.impl.xml.XmlWorldProperties;
import pl.north93.northplatform.api.bukkit.BukkitApiCore;
import pl.north93.northplatform.api.bukkit.tick.ITickableManager;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;

@Slf4j
public class PropertiesManagerImpl implements IWorldPropertiesManager, Listener
{
    private final PropertiesConfig serverConfig = new PropertiesConfig(null);
    private final Map<String, WorldPropertiesImpl> propertiesByWorld = new HashMap<>();
    private final Map<Player, IPlayerProperties> playerProperties = new HashMap<>();
    
    private final BukkitApiCore apiCore;
    
    @Bean
    private PropertiesManagerImpl(BukkitApiCore apiCore, ITickableManager tickableManager)
    {
        this.apiCore = apiCore;
        tickableManager.addTickableObjectsCollection(propertiesByWorld.values());
        apiCore.registerEvents(this);
    }
    
    @Override
    public PropertiesConfig getServerConfig()
    {
        return serverConfig;
    }

    @Override
    public PropertiesConfig getWorldConfig(String worldName)
    {
        Preconditions.checkArgument(worldName != null, "World name cannot be null");
        
        return Optional.ofNullable(getProperties(worldName)).map(IWorldProperties::getWorldConfig).orElse(null);
    }

    @Override
    public PropertiesConfig getWorldConfig(World world)
    {
        Preconditions.checkArgument(world != null, "World cannot be null");
        
        return Optional.ofNullable(getProperties(world)).map(IWorldProperties::getWorldConfig).orElse(null);
    }

    @Override
    public IWorldProperties getProperties(String worldName)
    {
        Preconditions.checkArgument(worldName != null, "World name cannot be null");
        return propertiesByWorld.get(worldName);
    }

    @Override
    public IWorldProperties getProperties(World world)
    {
        Preconditions.checkArgument(world != null, "World cannot be null");
        return propertiesByWorld.get(world.getName());
    }
    
    @Override
    public IPlayerProperties getPlayerProperties(String playerName)
    {
        Preconditions.checkArgument(playerName != null, "Player name cannot be null");
        return getPlayerProperties(Bukkit.getPlayerExact(playerName));
    }

    @Override
    public IPlayerProperties getPlayerProperties(Player player)
    {
        Preconditions.checkArgument(player != null, "Player cannot be null");
        
        return playerProperties.computeIfAbsent(player, PlayerPropertiesImpl::new);
    }
    
    @Override
    public void reloadServerConfig()
    {
        serverConfig.setDefaultValues();
        
        try
        {
            File xmlFile = new File(apiCore.getRootDirectory(), "world-properties.xml");
            System.out.println(xmlFile.getAbsolutePath());
            System.out.println(new File(".").getAbsolutePath());
            if ( xmlFile.isFile() )
            {
                JAXB.unmarshal(xmlFile, XmlWorldProperties.class).applyToConfig(serverConfig);
            }
            else
            {
                log.warn("world-properties.xml in server directory doesn't exist, using default properties");
            }
            
            log.info("Reloaded server world-properties.xml");
            log.debug("server world-properties config: {}", serverConfig);
        }
        catch ( Throwable e )
        {
            log.error("An error occured while reloading server world-properties.xml. Using default properties", e);
        }
    }
    
    public void addWorldProperties(World world)
    {
        WorldPropertiesImpl properties = new WorldPropertiesImpl(world);
        properties.reloadWorldConfig();
        properties.updateWorld();
        
        propertiesByWorld.put(properties.getWorld().getName(), properties);
        log.debug("Added world properties for world {}", properties.getWorld());
    }
    
    public void removeWorldPropertiesForWorld(World world)
    {
        propertiesByWorld.remove(world.getName());
        log.debug("Removed properties for world {}", world.getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void removePlayerData(PlayerQuitEvent event)
    {
        playerProperties.remove(event.getPlayer());
    }
}
