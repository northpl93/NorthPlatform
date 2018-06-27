package pl.arieals.minigame.goldhunter.effect;

import org.bukkit.attribute.Attribute;

import pl.arieals.minigame.goldhunter.player.Effect;
import pl.north93.zgame.api.bukkit.tick.Tick;

public class HealingEffect extends Effect
{
    {
        setBarColor(EffectBarColor.GREEN);
    }
    
    // TODO: make this effect more general 
    
    @Tick
    private void heal()
    {
        double health = getPlayer().getPlayer().getHealth();
        double maxHealth = getPlayer().getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        
        getPlayer().getPlayer().setHealth(Math.min(maxHealth, health + 0.1));
        
        getPlayer().getMinecraftPlayer().setAbsorptionHearts(Math.min(6, getPlayer().getMinecraftPlayer().getAbsorptionHearts() + 0.1f));
    }
}
