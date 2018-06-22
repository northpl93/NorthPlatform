package pl.arieals.minigame.goldhunter.abilities;

import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;

import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.GoldHunterLogger;
import pl.arieals.minigame.goldhunter.arena.GoldHunterArena;
import pl.arieals.minigame.goldhunter.effect.BombArrowEffect;
import pl.arieals.minigame.goldhunter.effect.PoisonArrowEffect;
import pl.arieals.minigame.goldhunter.entity.BombArrow;
import pl.arieals.minigame.goldhunter.entity.PoisonArrow;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.zgame.api.bukkit.utils.AutoListener;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class HomingArrowAbility implements AutoListener
{
    @Inject
    @GoldHunterLogger
    private Logger logger;
    
    @Inject
    private GoldHunter goldHunter;
    
    @EventHandler
    public void onShootArrow(EntityShootBowEvent event)
    {
        if ( !( event.getEntity() instanceof Player ) )
        {
            return;
        }
        
        GoldHunterPlayer player = goldHunter.getPlayer((Player) event.getEntity());
        if ( player.getEffectTracker().removeEffect(PoisonArrowEffect.class) )
        {
            PoisonArrow arrow = new PoisonArrow(player.getPlayer().getWorld(), player.getPlayer(), findNearestTarget(player));
            arrow.shoot(event.getForce());
            event.setProjectile(arrow.getBukkitEntity());
        }
        if ( player.getEffectTracker().removeEffect(BombArrowEffect.class) )
        {
            BombArrow arrow = new BombArrow(player.getPlayer().getWorld(), player.getPlayer(), findNearestTarget(player));
            arrow.shoot(event.getForce());
            event.setProjectile(arrow.getBukkitEntity());
        }
    }
    
    private Player findNearestTarget(GoldHunterPlayer shooter)
    {
        GoldHunterArena arena = shooter.getArena();
        Player shooterEntity = shooter.getPlayer();
        
        Player result = null;
        double resultDistance = 64 * 64;
        
        for ( GoldHunterPlayer p : arena.getSignedPlayers() )
        {
            Player entity = p.getPlayer();
            
            double distance = entity.getLocation().distanceSquared(shooterEntity.getLocation());
            if ( distance > resultDistance )
            {
                continue;
            }
            
            if ( p.getTeam() != shooter.getTeam().opositeTeam() || !entity.canSee(shooterEntity) )
            {
                continue;
            }
            
            if ( !shooterEntity.hasLineOfSight(entity) )
            {
                continue;
            }
            
            result = entity;
            resultDistance = distance; 
        }
        
        logger.debug("find arrow target {} for shooter {}", result, shooter);
        return result;
    }
}
