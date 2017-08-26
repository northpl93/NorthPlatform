package pl.arieals.minigame.goldhunter.abilities;

import pl.arieals.minigame.goldhunter.AbilityHandler;
import pl.arieals.minigame.goldhunter.GoldHunterPlayer;
import pl.arieals.minigame.goldhunter.effect.BridgeEffect;

public class BrigdeAbility implements AbilityHandler
{
    private static final int ABILITY_TIME = 140; // TODO:
    @Override
    public boolean onUse(GoldHunterPlayer player)
    {
        player.getAbilityTracker().suspendAbilityLoading();
        player.getEffectTracker().addEffect(new BridgeEffect(), ABILITY_TIME).onComplete(player.getAbilityTracker()::resetAbilityLoading);
        return true;
    }
}
