package pl.arieals.minigame.goldhunter.effect;

import pl.arieals.minigame.goldhunter.Effect;

public class BridgeEffect extends Effect
{
    {
        setBarColor(EffectBarColor.GREEN);
    }
    
    @Override
    protected void onStart()
    {
        getPlayer().setBuildBridgeActive(true);
    }
    
    @Override
    protected void onEnd()
    {
        getPlayer().setBuildBridgeActive(false);
    }
}
