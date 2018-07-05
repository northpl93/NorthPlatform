package pl.arieals.minigame.goldhunter.listener;

import java.util.Collection;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.Iterables;

import net.minecraft.server.v1_12_R1.EntityArrow;

import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.zgame.api.bukkit.utils.AutoListener;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class PlayerListener implements AutoListener
{
    // TODO: refactor this listener
    @Inject
    private GoldHunter goldHunter;
    
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
        
        for ( LivingEntity entity : event.getAffectedEntities() )
        {
            GoldHunterPlayer affectedPlayer = goldHunter.getPlayer(entity);
            if ( affectedPlayer != null && canAffect(player, affectedPlayer, event.getPotion().getEffects()) )
            {
                event.setIntensity(entity, 1);
            }
            else
            {
                event.setIntensity(entity, 0);
            }
        }
    }
        
    private boolean canAffect(GoldHunterPlayer shooter, GoldHunterPlayer target, Collection<PotionEffect> effects)
    {
        PotionEffect effect = Iterables.getFirst(effects, null);
        
        if ( effect == null )
        {
            return true;
        }
        
        boolean isTeammate = shooter.getTeam() == target.getTeam();
        if ( effect.getType().equals(PotionEffectType.HEAL) || effect.getType().equals(PotionEffectType.REGENERATION) )
        {
            if ( !target.getCurrentClass().canBeHealedByPotion() )
            {
                return false;
            }
            
            return isTeammate;
        }
        else if ( effect.getType().equals(PotionEffectType.HARM) || effect.getType().equals(PotionEffectType.POISON) )
        {
            return !isTeammate;
        }
        
        return false;
    }
}
