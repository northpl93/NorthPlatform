package pl.mcpiraci.world.properties.impl.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.WorldUnloadEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pl.mcpiraci.world.properties.impl.PropertiesManagerImpl;
import pl.north93.zgame.api.bukkit.utils.AutoListener;

public class WorldLoadUnloadListener implements AutoListener
{
    private static final Logger logger = LogManager.getLogger();
    
    private final PropertiesManagerImpl propertiesManager;
    
    private WorldLoadUnloadListener(PropertiesManagerImpl propertiesManager)
    {
        this.propertiesManager = propertiesManager;
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldUnload(WorldUnloadEvent event)
    {
        logger.debug("onWorldUnload() for {}", () -> event.getWorld().getName());
        
        propertiesManager.removeWorldPropertiesForWorld(event.getWorld());
    }
}
