package pl.mcpiraci.world.properties.impl;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pl.mcpiraci.world.properties.IPlayerProperties;
import pl.mcpiraci.world.properties.IWorldProperties;
import pl.mcpiraci.world.properties.IWorldPropertiesManager;
import pl.mcpiraci.world.properties.PropertiesConfig;
import pl.mcpiraci.world.properties.impl.xml.XmlWorldProperties;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.tick.ITickableManager;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;

public class PropertiesManagerImpl implements IWorldPropertiesManager, Listener
{
    private static Logger logger = LogManager.getLogger();
    
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
                logger.warn("world-properties.xml in server directory doesn't exist, using default properties");
            }
            
            logger.info("Reloaded server world-properties.xml");
            logger.debug("server world-properties config: {}", serverConfig);
        }
        catch ( Throwable e )
        {
            logger.error("An error occured while reloading server world-properties.xml. Using default properties", e);
        }
    }
    
    public void addWorldProperties(World world)
    {
        WorldPropertiesImpl properties = new WorldPropertiesImpl(world);
        properties.reloadWorldConfig();
        properties.updateWorld();
        
        propertiesByWorld.put(properties.getWorld().getName(), properties);
        logger.debug("Added world properties for world {}", () -> properties.getWorld());
    }
    
    public void removeWorldPropertiesForWorld(World world)
    {
        propertiesByWorld.remove(world.getName());
        logger.debug("Removed properties for world {}", () -> world.getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void removePlayerData(PlayerQuitEvent event)
    {
        playerProperties.remove(event.getPlayer());
    }
}
