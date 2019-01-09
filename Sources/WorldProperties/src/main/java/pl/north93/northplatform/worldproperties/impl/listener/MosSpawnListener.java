package pl.north93.northplatform.worldproperties.impl.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import pl.north93.northplatform.worldproperties.IWorldProperties;
import pl.north93.northplatform.worldproperties.IWorldPropertiesManager;
import pl.north93.northplatform.api.bukkit.utils.AutoListener;

public class MosSpawnListener implements AutoListener
{
    private final IWorldPropertiesManager propertiesManager;
    
    private MosSpawnListener(IWorldPropertiesManager propertiesManager)
    {
        this.propertiesManager = propertiesManager;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMobSpawn(CreatureSpawnEvent event)
    {
        if ( event.getSpawnReason() == SpawnReason.CUSTOM )
        {
            // allow plugin's custom spawned mobs
            return;
        }
        
        IWorldProperties properties = propertiesManager.getProperties(event.getLocation().getWorld());
        if ( !properties.isMobSpawningEnabled() )
        {
            event.setCancelled(true);
        }
    }
}
