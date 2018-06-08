package pl.mcpiraci.world.properties.impl;

import javax.xml.bind.JAXB;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

import com.google.common.base.Preconditions;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

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

public class PropertiesManagerImpl implements IWorldPropertiesManager
{
    private static Logger logger = LogManager.getLogger();
    
    private final PropertiesConfig serverConfig = new PropertiesConfig(null);
    private final Map<String, WorldPropertiesImpl> propertiesByWorld = new HashMap<>();
    private final Map<Player, IPlayerProperties> playerProperties = new WeakHashMap<>();
    
    private final BukkitApiCore apiCore;
    
    @Bean
    private PropertiesManagerImpl(BukkitApiCore apiCore, ITickableManager tickableManager)
    {
        this.apiCore = apiCore;
        tickableManager.addTickableObjectsCollection(propertiesByWorld.values());
    }
    
    @Override
    public PropertiesConfig getServerConfig()
    {
        return serverConfig;
    }

    @Override
    public PropertiesConfig getWorldConfig(String worldName)
    {
        Preconditions.checkNotNull(worldName, "World name cannot be null");
        
        return Optional.ofNullable(getProperties(worldName)).map(IWorldProperties::getWorldConfig).orElse(null);
    }

    @Override
    public PropertiesConfig getWorldConfig(World world)
    {
        Preconditions.checkNotNull(world, "World cannot be null");
        
        return Optional.ofNullable(getProperties(world)).map(IWorldProperties::getWorldConfig).orElse(null);
    }

    @Override
    public IWorldProperties getProperties(String worldName)
    {
        Preconditions.checkNotNull(worldName, "World name cannot be null");
        return getProperties(Bukkit.getWorld(worldName));
    }

    @Override
    public IWorldProperties getProperties(World world)
    {
        Preconditions.checkNotNull(world, "World cannot be null");
        return Optional.ofNullable(propertiesByWorld.get(world.getName())).orElseGet(() -> loadWorldProperties(world));
    }
    
    @Override
    public IPlayerProperties getPlayerProperties(String playerName)
    {
        Preconditions.checkNotNull(playerName, "Player name cannot be null");
        return getPlayerProperties(Bukkit.getPlayerExact(playerName));
    }

    @Override
    public IPlayerProperties getPlayerProperties(Player player)
    {
        Preconditions.checkNotNull(player, "Player cannot be null");
        
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
    
    private WorldPropertiesImpl loadWorldProperties(World world)
    {
        WorldPropertiesImpl properties = new WorldPropertiesImpl(world);
        this.propertiesByWorld.put(world.getName(), properties);

        properties.reloadWorldConfig();
        properties.updateWorld();
        
        logger.debug("Loaded world properties for world {}", () -> properties.getWorld());
        return properties;
    }
    
    public void removeWorldPropertiesForWorld(World world)
    {
        propertiesByWorld.remove(world.getName());
        logger.debug("Removed properties for world {}", () -> world.getName());
    }

    
}
