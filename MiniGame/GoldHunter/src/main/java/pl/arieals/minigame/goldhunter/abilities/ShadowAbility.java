package pl.arieals.minigame.goldhunter.abilities;

import pl.arieals.minigame.goldhunter.effect.ShadowEffect;
import pl.arieals.minigame.goldhunter.player.AbilityHandler;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;

public class ShadowAbility implements AbilityHandler
{
    @Override
    public boolean onUse(GoldHunterPlayer player)
    {
        player.getAbilityTracker().suspendAbilityLoading();
        player.getEffectTracker().addEffect(new ShadowEffect(), 70 + 10 * player.getShopItemLevel("assasin.abilityduration"))
                .onComplete(player.getAbilityTracker()::resetAbilityLoading);
        return true;
    }
}
