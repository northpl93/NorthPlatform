package pl.north93.northplatform.minigame.goldhunter.abilities;

import pl.north93.northplatform.minigame.goldhunter.effect.ShadowEffect;
import pl.north93.northplatform.minigame.goldhunter.player.AbilityHandler;
import pl.north93.northplatform.minigame.goldhunter.player.GoldHunterPlayer;

public class ShadowAbility implements AbilityHandler
{
    @Override
    public boolean onUse(GoldHunterPlayer player)
    {
        player.getEffectTracker().addEffect(new ShadowEffect(), 70 + 10 * player.getShopItemLevel("assasin.abilityduration"));
        return true;
    }
}
