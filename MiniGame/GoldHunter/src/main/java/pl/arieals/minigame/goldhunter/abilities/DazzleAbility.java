package pl.arieals.minigame.goldhunter.abilities;

import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerFishEvent;

import pl.arieals.minigame.goldhunter.AbilityHandler;
import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.GoldHunterPlayer;
import pl.arieals.minigame.goldhunter.effect.BlindnessEffect;
import pl.arieals.minigame.goldhunter.effect.DazzleAbilityEffect;
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
    
    /*
    @EventHandler
    public void onFishHookDamage(EntityDamageByEntityEvent event)
    {
        if ( !( event.getDamager() instanceof FishHook ) && !( event.getEntity() instanceof Player ) )
        {
            return;
        }
        
        FishHook hook = (FishHook) event.getDamager();
        
        if ( !( hook.getShooter() instanceof Player ) )
        {
            return;
        }
        
        GoldHunterPlayer shooter = goldHunter.getPlayer((Player) hook.getShooter());
        
        if ( shooter.getEffectTracker().removeEffect(DazzleAbilityEffect.class) )
        {
            GoldHunterPlayer attacked = goldHunter.getPlayer((Player) event.getEntity());
            attacked.getEffectTracker().addEffect(new BlindnessEffect(), 50); // TODO: add level up
        }
    } */
    
    @EventHandler
    public void onFishHookDamage(PlayerFishEvent event)
    {
        System.out.println(event.getState());
    }
}
