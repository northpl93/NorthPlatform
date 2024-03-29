package pl.north93.northplatform.minigame.goldhunter.effect;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pl.north93.northplatform.minigame.goldhunter.player.Effect;

public class StrengthEffect extends Effect
{
    {
        setBarColor(EffectBarColor.GREEN);
    }
    
    @Override
    protected void onStart()
    {
        getPlayer().getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, true, false), true);
    }
    
    @Override
    protected void onEnd()
    {
        getPlayer().getPlayer().removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
    }
}
