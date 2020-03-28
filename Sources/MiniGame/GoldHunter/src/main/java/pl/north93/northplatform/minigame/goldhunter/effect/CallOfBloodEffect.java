package pl.north93.northplatform.minigame.goldhunter.effect;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pl.north93.northplatform.minigame.goldhunter.player.Effect;

public class CallOfBloodEffect extends AbilityEffect
{
    public CallOfBloodEffect()
    {
        setBarColor(Effect.EffectBarColor.GREEN);
    }
    
    @Override
    protected void onStart()
    {
        getPlayer().getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
        getPlayer().getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, false, false));
        getPlayer().getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false));
    }
    
    @Override
    protected void onEnd()
    {
        getPlayer().getPlayer().removePotionEffect(PotionEffectType.SPEED);
        getPlayer().getPlayer().removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        getPlayer().getPlayer().removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
    }
}
