package pl.north93.northplatform.worldproperties.impl.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import pl.north93.northplatform.api.bukkit.server.AutoListener;
import pl.north93.northplatform.worldproperties.IPlayerProperties;
import pl.north93.northplatform.worldproperties.IWorldPropertiesManager;

public class PlayerBuildListener implements AutoListener
{
    private final IWorldPropertiesManager propertiesManager;
    
    private PlayerBuildListener(IWorldPropertiesManager propertiesManager)
    {
        this.propertiesManager = propertiesManager;
    }
    
    private void handleEvent(Player player, Cancellable event)
    {
        IPlayerProperties playerProperties = propertiesManager.getPlayerProperties(player);
        
        if ( !playerProperties.effectiveCanBuild() )
        {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event)
    {
        handleEvent(event.getPlayer(), event);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDestroy(BlockBreakEvent event)
    {
        handleEvent(event.getPlayer(), event);
    }
}
