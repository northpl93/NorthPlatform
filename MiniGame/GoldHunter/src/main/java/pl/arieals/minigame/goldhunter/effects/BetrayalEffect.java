package pl.arieals.minigame.goldhunter.effects;

import pl.arieals.minigame.goldhunter.Effect;

public class BetrayalEffect extends Effect
{
    {
        setBarColor(EffectBarColor.GREEN);
    }
    
    @Override
    protected void onStart()
    {
        getPlayer().setDisplayTeam(getPlayer().getTeam().opositeTeam());
    }
    
    @Override
    protected void onEnd()
    {
        getPlayer().setDisplayTeam(getPlayer().getTeam());
    }
}
