package pl.north93.northplatform.worldproperties.impl.listener;

import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.LeavesDecayEvent;

import pl.north93.northplatform.worldproperties.IWorldProperties;
import pl.north93.northplatform.worldproperties.IWorldPropertiesManager;
import pl.north93.northplatform.api.bukkit.utils.AutoListener;

public class BlockPhysicsListener implements AutoListener
{
    private final IWorldPropertiesManager propertiesManager;
    
    private BlockPhysicsListener(IWorldPropertiesManager propertiesManager)
    {
        this.propertiesManager = propertiesManager;
    }
    
    private void handleEvent(World world, Cancellable event)
    {
        IWorldProperties properties = propertiesManager.getProperties(world);
        
        if ( properties != null && !properties.isPhysicsEnabled() )
        {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleBlockPhysics(BlockPhysicsEvent event)
    {
        handleEvent(event.getBlock().getWorld(), event);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockFormEvent(BlockGrowEvent event)
    {
        handleEvent(event.getBlock().getWorld(), event);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeavesDecay(LeavesDecayEvent event)
    {
        handleEvent(event.getBlock().getWorld(), event);
    }
}
