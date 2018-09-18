package pl.arieals.minigame.goldhunter.effect;

import pl.arieals.minigame.goldhunter.player.Effect;

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
