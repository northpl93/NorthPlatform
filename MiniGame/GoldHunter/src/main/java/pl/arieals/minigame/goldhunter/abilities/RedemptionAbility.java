package pl.arieals.minigame.goldhunter.abilities;

import java.util.Arrays;
import java.util.List;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pl.arieals.minigame.goldhunter.effect.BlindnessEffect;
import pl.arieals.minigame.goldhunter.effect.PoisonEffect;
import pl.arieals.minigame.goldhunter.player.AbilityHandler;
import pl.arieals.minigame.goldhunter.player.Effect;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;

public class RedemptionAbility implements AbilityHandler
{
    private static final List<Class<? extends Effect>> negativeEffects = Arrays.asList(PoisonEffect.class, BlindnessEffect.class);
    
    @Override
    public boolean onUse(GoldHunterPlayer player)
    {
        player.getPlayer().setFireTicks(0);
        player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, 20));
        
        negativeEffects.forEach(player.getEffectTracker()::removeEffect);
        return true;
    }
}
