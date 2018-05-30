package pl.mcpiraci.world.properties.impl.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import pl.mcpiraci.world.properties.PlayerProperties;
import pl.mcpiraci.world.properties.WorldPropertiesManager;
import pl.north93.zgame.api.bukkit.utils.AutoListener;

public class PlayerInteractListener implements AutoListener
{
    private final WorldPropertiesManager propertiesManager;
    
    private PlayerInteractListener(WorldPropertiesManager propertiesManager)
    {
        this.propertiesManager = propertiesManager;
    }
    
    private void handleEvent(Player player, Cancellable event)
    {
        PlayerProperties playerProperties = propertiesManager.getPlayerProperties(player);
        
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
}
