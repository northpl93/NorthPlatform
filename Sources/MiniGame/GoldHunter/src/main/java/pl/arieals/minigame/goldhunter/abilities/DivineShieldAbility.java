package pl.arieals.minigame.goldhunter.abilities;

import java.util.Arrays;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pl.arieals.minigame.goldhunter.effect.BlindnessEffect;
import pl.arieals.minigame.goldhunter.effect.DeathEffect;
import pl.arieals.minigame.goldhunter.effect.DivineShieldEffect;
import pl.arieals.minigame.goldhunter.effect.PoisonEffect;
import pl.arieals.minigame.goldhunter.event.EffectAttachEvent;
import pl.arieals.minigame.goldhunter.player.AbilityHandler;
import pl.arieals.minigame.goldhunter.player.Effect;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;

public class DivineShieldAbility implements AbilityHandler
{
    private static final List<Class<? extends Effect>> negativeEffects = Arrays.asList(PoisonEffect.class, BlindnessEffect.class, DeathEffect.class);
    
    @Override
    public boolean onUse(GoldHunterPlayer player)
    {
        player.getPlayer().setFireTicks(0);
        player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, 20));
        
        negativeEffects.forEach(player.getEffectTracker()::removeEffect);
        
        player.getEffectTracker().addEffect(new DivineShieldEffect(), 60);
        return true;
    }
    
    @EventHandler
    public void onEffectAttach(EffectAttachEvent event)
    {
        if ( event.getGoldHunterPlayer().getEffectTracker().hasEffectOfType(DivineShieldEffect.class) 
                && negativeEffects.contains(event.getEffectType()) )
        {
            event.setCancelled(true);
        }
    }
}
