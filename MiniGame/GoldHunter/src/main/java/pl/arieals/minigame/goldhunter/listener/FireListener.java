package pl.arieals.minigame.goldhunter.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;

import pl.north93.zgame.api.bukkit.utils.AutoListener;

public class FireListener implements AutoListener
{
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFireSpread(BlockIgniteEvent event)
    {
        if ( event.getCause() == IgniteCause.LAVA || event.getCause() == IgniteCause.SPREAD )
        {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBurn(BlockBurnEvent event)
    {
        event.setCancelled(true);
    }
}
