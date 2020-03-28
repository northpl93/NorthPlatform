package pl.north93.northplatform.minigame.goldhunter.effect;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pl.north93.northplatform.minigame.goldhunter.player.Effect;

public class BridgeEffect extends AbilityEffect
{
    {
        setBarColor(Effect.EffectBarColor.GREEN);
    }
    
    @Override
    protected void onStart()
    {
        super.onStart();
        
        getPlayer().setBuildBridgeActive(true);
        getPlayer().getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128));
    }
    
    @Override
    protected void onEnd()
    {
        super.onEnd();
        
        getPlayer().setBuildBridgeActive(false);
        getPlayer().getPlayer().removePotionEffect(PotionEffectType.JUMP);
    }
}
