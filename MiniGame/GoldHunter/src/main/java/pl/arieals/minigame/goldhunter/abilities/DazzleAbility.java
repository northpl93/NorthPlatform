package pl.arieals.minigame.goldhunter.abilities;

import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;

import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.effect.BlindnessEffect;
import pl.arieals.minigame.goldhunter.effect.DazzleAbilityEffect;
import pl.arieals.minigame.goldhunter.player.AbilityHandler;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class DazzleAbility implements AbilityHandler
{
    @Inject
    private static GoldHunter goldHunter;
    
    @Override
    public boolean onUse(GoldHunterPlayer player)
    {
        player.getAbilityTracker().suspendAbilityLoading();
        player.getEffectTracker().addEffect(new DazzleAbilityEffect()).onComplete(player.getAbilityTracker()::resetAbilityLoading);
        return false;
    }
    
    @EventHandler
    public void onHitByFishHook(ProjectileHitEvent event)
    {
        if ( !( event.getEntity() instanceof FishHook ) || !( event.getHitEntity() instanceof Player ) )
        {
            return;
        }
        
        FishHook hook = (FishHook) event.getEntity();
        
        if ( !( hook.getShooter() instanceof Player ) )
        {
            return;
        }
        
        GoldHunterPlayer shooter = goldHunter.getPlayer((Player) hook.getShooter());
        GoldHunterPlayer attacked = goldHunter.getPlayer((Player) event.getHitEntity());
        
        if ( attacked != null && shooter != null && shooter.getEffectTracker().removeEffect(DazzleAbilityEffect.class) )
        {
            attacked.getEffectTracker().addEffect(new BlindnessEffect(), 50 + 10 * shooter.getShopItemLevel("scout.slinger.time2"));
        }
    }
}
