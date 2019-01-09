package pl.arieals.minigame.goldhunter.abilities;

import pl.arieals.minigame.goldhunter.effect.SirenTearsEffect;
import pl.arieals.minigame.goldhunter.player.AbilityHandler;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;

public class SirenTearsAbility implements AbilityHandler
{
    private static final int BASE_DURATION = 100;
    private static final int PER_LEVEL = 10;
    
    @Override
    public boolean onUse(GoldHunterPlayer player)
    {
        int duration = BASE_DURATION + player.getShopItemLevel("medic.abilityduration") * PER_LEVEL;
        player.getEffectTracker().addEffect(new SirenTearsEffect(10), duration);
        return true;
    }
}
