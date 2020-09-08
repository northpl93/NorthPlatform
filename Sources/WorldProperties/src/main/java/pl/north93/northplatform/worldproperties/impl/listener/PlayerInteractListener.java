package pl.north93.northplatform.worldproperties.impl.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import pl.north93.northplatform.api.bukkit.server.AutoListener;
import pl.north93.northplatform.worldproperties.IPlayerProperties;
import pl.north93.northplatform.worldproperties.IWorldPropertiesManager;

public class PlayerInteractListener implements AutoListener
{
    private final IWorldPropertiesManager propertiesManager;
    
    private PlayerInteractListener(IWorldPropertiesManager propertiesManager)
    {
        this.propertiesManager = propertiesManager;
    }
    
    private void handleEvent(Player player, Cancellable event)
    {
        IPlayerProperties playerProperties = propertiesManager.getPlayerProperties(player);
        
        if ( !playerProperties.effectiveCanInteract() )
        {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event)
    {
        handleEvent(event.getPlayer(), event);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEntityEvent event)
    {
        handleEvent(event.getPlayer(), event);
    }
    
    @EventHandler
    public void onInteract(PlayerArmorStandManipulateEvent event)
    {
        handleEvent(event.getPlayer(), event);
    }
}
