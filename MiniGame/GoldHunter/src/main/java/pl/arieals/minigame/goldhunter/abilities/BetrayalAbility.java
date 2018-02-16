package pl.arieals.minigame.goldhunter.abilities;

import pl.arieals.minigame.goldhunter.AbilityHandler;
import pl.arieals.minigame.goldhunter.GoldHunterPlayer;
import pl.arieals.minigame.goldhunter.effect.BetrayalEffect;

public class BetrayalAbility implements AbilityHandler
{
    @Override
    public boolean onUse(GoldHunterPlayer player)
    {
        player.getAbilityTracker().suspendAbilityLoading();
        player.getEffectTracker().addEffect(new BetrayalEffect(), 300 + 100 * player.getShopItemLevel("svip.spy.time2"))
                .onComplete(player.getAbilityTracker()::resetAbilityLoading);
        return true;
    }
}
