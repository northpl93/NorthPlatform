package pl.north93.northplatform.minigame.goldhunter.abilities;

import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;

import pl.north93.northplatform.minigame.goldhunter.GoldHunter;
import pl.north93.northplatform.minigame.goldhunter.effect.BlindnessEffect;
import pl.north93.northplatform.minigame.goldhunter.effect.DazzleAbilityEffect;
import pl.north93.northplatform.minigame.goldhunter.player.AbilityHandler;
import pl.north93.northplatform.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

public class DazzleAbility implements AbilityHandler
{
    @Inject
    private static GoldHunter goldHunter;
    
    @Override
    public boolean onUse(GoldHunterPlayer player)
    {
        player.getEffectTracker().addEffect(new DazzleAbilityEffect());
        return false;
    }
    
    @EventHandler
    public void onHitByFishHook(ProjectileHitEvent event)
    {
        if ( !( event.getEntity() instanceof Snowball ) || !( event.getHitEntity() instanceof Player ) )
        {
            return;
        }
        
        Snowball ball = (Snowball) event.getEntity();
        
        if ( !( ball.getShooter() instanceof Player ) )
        {
            return;
        }
        
        GoldHunterPlayer shooter = goldHunter.getPlayer((Player) ball.getShooter());
        GoldHunterPlayer attacked = goldHunter.getPlayer((Player) event.getHitEntity());
        
        if ( attacked != null && shooter != null && attacked.getTeam() != shooter.getTeam() && shooter.getEffectTracker().removeEffect(DazzleAbilityEffect.class) )
        {
            attacked.getEffectTracker().addEffect(new BlindnessEffect(), 50 + 10 * shooter.getShopItemLevel("scout.slinger.time2"));
        }
    }
}
