package pl.mcpiraci.world.properties.impl.listener;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.WorldUnloadEvent;

import pl.mcpiraci.world.properties.impl.PropertiesManagerImpl;
import pl.mcpiraci.world.properties.impl.WorldPropertiesComponent;
import pl.north93.zgame.api.bukkit.server.IWorldInitializer;
import pl.north93.zgame.api.bukkit.utils.AutoListener;

public class WorldLoadUnloadListener implements AutoListener, IWorldInitializer
{
    private static final Logger logger = LogManager.getLogger();
    
    private final PropertiesManagerImpl propertiesManager;
    
    private WorldLoadUnloadListener(PropertiesManagerImpl propertiesManager)
    {
        this.propertiesManager = propertiesManager;
    }
    
    @Override
    public void initialiseWorld(World world, File directory)
    {
        if ( !WorldPropertiesComponent.isEnabled() )
        {
            return;
        }
        
        logger.debug("initialiseWorld() for {}", () -> world.getName());
        
        propertiesManager.addWorldProperties(world);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldUnload(WorldUnloadEvent event)
    {
        logger.debug("onWorldUnload() for {}", () -> event.getWorld().getName());
        
        propertiesManager.removeWorldPropertiesForWorld(event.getWorld());
    } 
}
