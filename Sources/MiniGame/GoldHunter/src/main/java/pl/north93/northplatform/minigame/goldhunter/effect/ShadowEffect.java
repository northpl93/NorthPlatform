package pl.north93.northplatform.minigame.goldhunter.effect;

import pl.north93.northplatform.minigame.goldhunter.player.Effect;

public class ShadowEffect extends AbilityEffect
{
    {
        setBarColor(Effect.EffectBarColor.GREEN);
    }
    
    @Override
    protected void onStart()
    {
        getPlayer().setShadow(true);
    }
    
    @Override
    protected void onEnd()
    {
        getPlayer().setShadow(false);
    }
}
