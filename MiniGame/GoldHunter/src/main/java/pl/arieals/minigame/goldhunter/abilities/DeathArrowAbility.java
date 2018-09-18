package pl.arieals.minigame.goldhunter.abilities;

import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;

import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.GoldHunterLogger;
import pl.arieals.minigame.goldhunter.effect.DeathArrowEffect;
import pl.arieals.minigame.goldhunter.entity.DeathArrow;
import pl.arieals.minigame.goldhunter.player.AbilityHandler;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class DeathArrowAbility implements AbilityHandler
{
    @Inject
    @GoldHunterLogger
    private static Logger logger;
    
    @Inject
    private static GoldHunter goldHunter;
    
    @Override
    public boolean onUse(GoldHunterPlayer player)
    {
        player.getEffectTracker().addEffect(new DeathArrowEffect());
        return true;
    }
    
    @EventHandler
    public void onShootArrow(EntityShootBowEvent event)
    {
        if ( !( event.getEntity() instanceof Player ) )
        {
            return;
        }
        
        GoldHunterPlayer player = goldHunter.getPlayer((Player) event.getEntity());
        if ( player != null && player.getEffectTracker().removeEffect(DeathArrowEffect.class) )
        {
            DeathArrow arrow = new DeathArrow(player.getPlayer().getWorld(), player.getPlayer());
            arrow.shoot(event.getForce());
            event.setProjectile(arrow.getBukkitEntity());
            
            logger.debug("{} has shot death arrow", () -> player.getPlayer().getName());
        }
    }
}
