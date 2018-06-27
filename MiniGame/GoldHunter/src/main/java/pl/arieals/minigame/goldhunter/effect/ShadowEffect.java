package pl.arieals.minigame.goldhunter.effect;

import pl.arieals.minigame.goldhunter.player.Effect;

public class ShadowEffect extends Effect
{
    {
        setBarColor(EffectBarColor.GREEN);
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
