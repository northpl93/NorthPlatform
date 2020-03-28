package pl.north93.northplatform.minigame.goldhunter.abilities;

import pl.north93.northplatform.minigame.goldhunter.effect.BattlecryEffect;
import pl.north93.northplatform.minigame.goldhunter.player.AbilityHandler;
import pl.north93.northplatform.minigame.goldhunter.player.GoldHunterPlayer;

public class BattlecryAbility implements AbilityHandler
{
    @Override
    public boolean onUse(GoldHunterPlayer player)
    {
        player.getEffectTracker().addEffect(new BattlecryEffect(10), 100 + 20 * player.getShopItemLevel("shaman.abilityduration"));
        return true;
    }
}
