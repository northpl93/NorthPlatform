package pl.north93.northplatform.minigame.goldhunter.effect;

import pl.north93.northplatform.minigame.goldhunter.player.Effect;

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
