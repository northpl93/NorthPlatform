package pl.north93.northplatform.minigame.goldhunter.effect;

import pl.north93.northplatform.minigame.goldhunter.player.Effect;

public abstract class AbilityEffect extends Effect
{
    @Override
    protected void onStart()
    {
        getPlayer().getAbilityTracker().suspendAbilityLoading();
    }
    
    @Override
    protected void onEnd()
    {
        getPlayer().getAbilityTracker().resetAbilityLoading();
    }
}
