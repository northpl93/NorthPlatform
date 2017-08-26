package pl.arieals.minigame.goldhunter.effect;

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
        getPlayer().getPlayer().setHealth(getPlayer().getPlayer().getHealth() + 0.025);
    }
}
