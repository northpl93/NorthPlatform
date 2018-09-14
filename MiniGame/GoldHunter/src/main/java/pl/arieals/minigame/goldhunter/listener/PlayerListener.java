package pl.arieals.minigame.goldhunter.listener;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PotionSplashEvent;

import net.minecraft.server.v1_12_R1.EntityArrow;

import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.arieals.minigame.goldhunter.player.PotionManager;
import pl.north93.zgame.api.bukkit.utils.AutoListener;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class PlayerListener implements AutoListener
{
    // TODO: refactor this listener
    
    @Inject
    private GoldHunter goldHunter;
    @Inject
    private PotionManager potionManager;
    
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
    
    @EventHandler(priority = EventPriority.LOW)
    public void disableMinecraftSplashPotion(PotionSplashEvent event)
    {
        if ( !goldHunter.isGameWorld(event.getPotion().getWorld()) )
        {
            return;
        }
        
        event.getAffectedEntities().forEach(entity -> event.setIntensity(entity, 0));
    }
    
    @EventHandler
    public void potionSplashEvent(PotionSplashEvent event)
    {
        if ( !( event.getPotion().getShooter() instanceof Player ) )
        {
            return;
        }
        
        GoldHunterPlayer player = goldHunter.getPlayer((Player) event.getPotion().getShooter());
        if ( player == null )
        {
            return;
        }
        
        potionManager.splashPotion(player, event.getPotion());
    }
}
