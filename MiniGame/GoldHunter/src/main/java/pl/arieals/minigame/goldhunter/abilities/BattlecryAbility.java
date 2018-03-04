package pl.arieals.minigame.goldhunter.abilities;

import pl.arieals.minigame.goldhunter.effect.BattlecryEffect;
import pl.arieals.minigame.goldhunter.player.AbilityHandler;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;

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
