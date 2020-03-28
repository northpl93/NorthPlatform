package pl.north93.northplatform.minigame.goldhunter.abilities;

import java.util.Arrays;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pl.north93.northplatform.minigame.goldhunter.effect.BlindnessEffect;
import pl.north93.northplatform.minigame.goldhunter.effect.DeathEffect;
import pl.north93.northplatform.minigame.goldhunter.effect.DivineShieldEffect;
import pl.north93.northplatform.minigame.goldhunter.effect.PoisonEffect;
import pl.north93.northplatform.minigame.goldhunter.event.EffectAttachEvent;
import pl.north93.northplatform.minigame.goldhunter.player.AbilityHandler;
import pl.north93.northplatform.minigame.goldhunter.player.Effect;
import pl.north93.northplatform.minigame.goldhunter.player.GoldHunterPlayer;

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
