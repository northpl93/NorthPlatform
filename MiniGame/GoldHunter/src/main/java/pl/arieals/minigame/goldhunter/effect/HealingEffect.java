package pl.arieals.minigame.goldhunter.effect;

import org.bukkit.attribute.Attribute;

import pl.arieals.minigame.goldhunter.Effect;
import pl.north93.zgame.api.bukkit.tick.Tick;

public class HealingEffect extends Effect
{
    {
        setBarColor(EffectBarColor.GREEN);
    }
    
    @Tick
    private void heal()
    {
        double health = getPlayer().getPlayer().getHealth();
        double maxHealth = getPlayer().getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        
        getPlayer().getPlayer().setHealth(Math.min(maxHealth, health + 0.025));
    }
}
