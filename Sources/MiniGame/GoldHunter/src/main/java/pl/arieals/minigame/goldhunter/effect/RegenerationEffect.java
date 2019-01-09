package pl.arieals.minigame.goldhunter.effect;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import pl.arieals.minigame.goldhunter.player.Effect;
import pl.north93.zgame.api.bukkit.tick.Tick;

@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RegenerationEffect extends Effect
{
    private final double hpPerSecond;
    private final double absorptionPerSecond;
    
    {
        setBarColor(EffectBarColor.GREEN);
    }
    
    public RegenerationEffect(double hpPerSecond)
    {
        this(hpPerSecond, 0);
    }
    
    @Tick
    private void regenerate()
    {
        getPlayer().heal(hpPerSecond / 20.0);
        
        getPlayer().getMinecraftPlayer().setAbsorptionHearts(Math.min(6, getPlayer().getMinecraftPlayer().getAbsorptionHearts() + (float) absorptionPerSecond / 20.0f));
    }
}
