package pl.arieals.minigame.goldhunter.effect;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pl.arieals.minigame.goldhunter.Effect;

public class ShadowEffect extends Effect
{
    {
        setBarColor(EffectBarColor.GREEN);
    }
    
    @Override
    protected void onStart()
    {
        getPlayer().setShadow(true);
        getPlayer().getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, true, false), true);
    }
    
    @Override
    protected void onEnd()
    {
        getPlayer().setShadow(false);
        getPlayer().getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
    }
}
