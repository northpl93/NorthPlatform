package pl.arieals.minigame.goldhunter.abilities;

import pl.arieals.minigame.goldhunter.AbilityHandler;
import pl.arieals.minigame.goldhunter.GoldHunterPlayer;
import pl.arieals.minigame.goldhunter.effect.BattlecryEffect;

public class BattlecryAbility implements AbilityHandler
{
    @Override
    public boolean onUse(GoldHunterPlayer player)
    {
        player.getAbilityTracker().suspendAbilityLoading();
        player.getEffectTracker().addEffect(new BattlecryEffect(10), 200).onComplete(player.getAbilityTracker()::resetAbilityLoading);
        return true;
    }
}
