package pl.arieals.minigame.goldhunter;

import pl.arieals.minigame.goldhunter.effect.SirenTearsEffect;

public class SirenTearsAbility implements AbilityHandler
{
    @Override
    public boolean onUse(GoldHunterPlayer player)
    {
        player.getAbilityTracker().suspendAbilityLoading();
        player.getEffectTracker().addEffect(new SirenTearsEffect(10), 320).onComplete(player.getAbilityTracker()::resetAbilityLoading);
        return true;
    }
}
