package pl.arieals.minigame.goldhunter.effect;

public class ShadowEffect extends AbilityEffect
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
