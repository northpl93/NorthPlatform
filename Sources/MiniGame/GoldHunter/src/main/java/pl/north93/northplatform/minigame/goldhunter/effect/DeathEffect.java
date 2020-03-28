package pl.north93.northplatform.minigame.goldhunter.effect;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pl.north93.northplatform.minigame.goldhunter.player.Effect;
import pl.north93.northplatform.api.bukkit.tick.Tick;

public class DeathEffect extends Effect
{
    {
        setBarColor(EffectBarColor.RED);
    }
    
    @Override
    protected void onStart()
    {
        getPlayer().getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.WITHER, Integer.MAX_VALUE, 1, false, false), true);
    }
    
    @Override
    protected void onEnd()
    {
        getPlayer().getPlayer().removePotionEffect(PotionEffectType.WITHER);
    }
    
    @Tick
    protected void damagePlayer()
    {
        //getPlayer().getPlayer().setHealth(Math.max(0, getPlayer().getPlayer().getHealth() - 0.33));
        getPlayer().getPlayer().damage(5);
    }
}
