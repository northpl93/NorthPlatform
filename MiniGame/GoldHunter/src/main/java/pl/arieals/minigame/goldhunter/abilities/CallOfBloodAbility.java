package pl.arieals.minigame.goldhunter.abilities;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.effect.CallOfBloodEffect;
import pl.arieals.minigame.goldhunter.player.AbilityHandler;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class CallOfBloodAbility implements AbilityHandler
{
    public static final int BASE_DURATION = 60;
    public static final int PER_LEVEL = 10;
    public static final double BLOOD_TRIBUTE = 5;
    
    @Inject
    public static GoldHunter goldHunter;
    
    @Override
    public boolean onUse(GoldHunterPlayer player)
    {
        player.getAbilityTracker().suspendAbilityLoading();
        int duration = BASE_DURATION + player.getShopItemLevel("berserker.abilityduration") * PER_LEVEL;
        player.getEffectTracker().addEffect(new CallOfBloodEffect(), duration).onComplete(player.getAbilityTracker()::resetAbilityLoading);
        player.getPlayer().damage(BLOOD_TRIBUTE);
        return true;
    }
    
    @EventHandler
    public void onDamage(EntityDamageEvent event)
    {
        //GoldHunterPlayer player = goldHunter.getPlayer(event.getEntity());
        //if ( player.getEffectTracker().hasEffectOfType(CallOfBloodEffect.class) )
        //{
        //    event.setCancelled(true);
        //}
    }
}
