package pl.arieals.minigame.goldhunter.effect;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BridgeEffect extends AbilityEffect
{
    {
        setBarColor(EffectBarColor.GREEN);
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
