package pl.north93.northplatform.worldproperties.impl.listener;

import java.io.File;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.WorldUnloadEvent;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.bukkit.server.AutoListener;
import pl.north93.northplatform.api.bukkit.server.IWorldInitializer;
import pl.north93.northplatform.worldproperties.impl.PropertiesManagerImpl;
import pl.north93.northplatform.worldproperties.impl.WorldPropertiesComponent;

@Slf4j
public class WorldLoadUnloadListener implements AutoListener, IWorldInitializer
{
    private final PropertiesManagerImpl propertiesManager;
    
    private WorldLoadUnloadListener(PropertiesManagerImpl propertiesManager)
    {
        this.propertiesManager = propertiesManager;
    }
    
    @Override
    public void initialiseWorld(World world, File directory)
    {
        if ( ! WorldPropertiesComponent.isEnabled() )
        {
            return;
        }
        
        log.debug("initialiseWorld() for {}", world.getName());
        
        propertiesManager.addWorldProperties(world);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldUnload(WorldUnloadEvent event)
    {
        log.debug("onWorldUnload() for {}", event.getWorld().getName());
        
        propertiesManager.removeWorldPropertiesForWorld(event.getWorld());
    } 
}
