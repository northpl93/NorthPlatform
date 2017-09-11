package pl.arieals.minigame.goldhunter.abilities;

import pl.arieals.minigame.goldhunter.AbilityHandler;
import pl.arieals.minigame.goldhunter.GoldHunterPlayer;
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
