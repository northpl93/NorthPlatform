package pl.north93.northplatform.minigame.goldhunter.abilities;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;

import org.slf4j.Logger;

import pl.north93.northplatform.minigame.goldhunter.GoldHunter;
import pl.north93.northplatform.minigame.goldhunter.GoldHunterLogger;
import pl.north93.northplatform.minigame.goldhunter.effect.DeathArrowEffect;
import pl.north93.northplatform.minigame.goldhunter.entity.DeathArrow;
import pl.north93.northplatform.minigame.goldhunter.player.AbilityHandler;
import pl.north93.northplatform.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

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
            final Player bukkitPlayer = player.getPlayer();

            DeathArrow arrow = new DeathArrow(bukkitPlayer.getWorld(), bukkitPlayer);
            arrow.shoot(event.getForce());
            event.setProjectile(arrow.getBukkitEntity());

            logger.debug("{} has shot death arrow", bukkitPlayer.getName());
        }
    }
}
