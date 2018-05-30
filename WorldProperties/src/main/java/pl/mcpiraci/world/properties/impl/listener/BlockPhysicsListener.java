package pl.mcpiraci.world.properties.impl.listener;

import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.LeavesDecayEvent;

import pl.mcpiraci.world.properties.WorldProperties;
import pl.mcpiraci.world.properties.WorldPropertiesManager;
import pl.north93.zgame.api.bukkit.utils.AutoListener;

public class BlockPhysicsListener implements AutoListener
{
    private final WorldPropertiesManager propertiesManager;
    
    private BlockPhysicsListener(WorldPropertiesManager propertiesManager)
    {
        this.propertiesManager = propertiesManager;
    }
    
    private void handleEvent(World world, Cancellable event)
    {
        WorldProperties properties = propertiesManager.getProperties(world);
        
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
