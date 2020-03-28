package pl.north93.northplatform.minigame.goldhunter.effect;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pl.north93.northplatform.minigame.goldhunter.player.Effect;

public class SlownessEffect extends Effect
{
    {
        setBarColor(EffectBarColor.RED);
    }
    
    // TODO: make it configurable
    // TODO: implement without minecraft's potion effect
    
    @Override
    protected void onStart()
    {
        getPlayer().getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0, false, false), true);
    }
    
    @Override
    protected void onEnd()
    {
        getPlayer().getPlayer().removePotionEffect(PotionEffectType.SLOW);
    }
}
