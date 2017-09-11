package pl.arieals.minigame.goldhunter;

import pl.arieals.minigame.goldhunter.effects.BetrayalEffect;

public class BetrayalAbility implements AbilityHandler
{
    @Override
    public boolean onUse(GoldHunterPlayer player)
    {
        player.getAbilityTracker().suspendAbilityLoading();
        player.getEffectTracker().addEffect(new BetrayalEffect(), 200).onComplete(player.getAbilityTracker()::resetAbilityLoading);
        return true;
    }
}
