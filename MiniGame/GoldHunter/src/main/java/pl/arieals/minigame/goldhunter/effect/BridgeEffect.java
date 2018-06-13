package pl.arieals.minigame.goldhunter.effect;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pl.arieals.minigame.goldhunter.player.Effect;

public class BridgeEffect extends Effect
{
    {
        setBarColor(EffectBarColor.GREEN);
    }
    
    @Override
    protected void onStart()
    {
        getPlayer().setBuildBridgeActive(true);
        getPlayer().getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128));
    }
    
    @Override
    protected void onEnd()
    {
        getPlayer().setBuildBridgeActive(false);
        getPlayer().getPlayer().removePotionEffect(PotionEffectType.JUMP);
    }
}
