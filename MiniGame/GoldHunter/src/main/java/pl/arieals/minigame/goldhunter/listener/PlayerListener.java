package pl.arieals.minigame.goldhunter.listener;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import net.minecraft.server.v1_12_R1.EntityArrow;

import pl.north93.zgame.api.bukkit.utils.AutoListener;

public class PlayerListener implements AutoListener
{
    
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event)
    {
        event.setFoodLevel(20);
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onShootArrow(EntityShootBowEvent event)
    {
        if ( !( event.getProjectile() instanceof Arrow ) )
        {
            return;
        }
        
        CraftArrow arrow = (CraftArrow) event.getProjectile();
        arrow.getHandle().fromPlayer = EntityArrow.PickupStatus.DISALLOWED;
    }
    
}
