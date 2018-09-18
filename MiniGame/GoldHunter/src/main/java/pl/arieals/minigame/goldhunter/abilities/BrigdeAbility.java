package pl.arieals.minigame.goldhunter.abilities;

import pl.arieals.minigame.goldhunter.effect.BridgeEffect;
import pl.arieals.minigame.goldhunter.player.AbilityHandler;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;

public class BrigdeAbility implements AbilityHandler
{
    private static final int BASE_DURATION = 30; // TODO:
    private static final int PER_LEVEL = 10;
    
    @Override
    public boolean onUse(GoldHunterPlayer player)
    {
        int duration = BASE_DURATION + player.getShopItemLevel("architect.abilityduration") * PER_LEVEL;
        player.getEffectTracker().addEffect(new BridgeEffect(), duration);
        return true;
    }
}
