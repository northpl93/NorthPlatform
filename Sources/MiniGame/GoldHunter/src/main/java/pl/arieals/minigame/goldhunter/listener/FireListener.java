package pl.arieals.minigame.goldhunter.listener;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;

import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.north93.zgame.api.bukkit.utils.AutoListener;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class FireListener implements AutoListener
{
    private static final List<Material> BURNABLE_MATERIALS = Arrays.asList(Material.WOOD, Material.WOOD_STEP, Material.WOOD_DOUBLE_STEP);
    
    @Inject
    private static GoldHunter goldHunter;
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFireSpread(BlockIgniteEvent event)
    {
        if ( !goldHunter.isGameWorld(event.getBlock().getWorld()) || event.getCause() == IgniteCause.LAVA 
                || event.getCause() == IgniteCause.SPREAD )
        {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBurn(BlockBurnEvent event)
    {
        if ( !goldHunter.isGameWorld(event.getBlock().getWorld()) 
                || !BURNABLE_MATERIALS.contains(event.getBlock().getType()) )
        {
            event.setCancelled(true);
        }
    }
}
