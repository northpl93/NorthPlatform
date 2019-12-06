package pl.arieals.minigame.goldhunter.abilities;

import org.bukkit.Location;

import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.effect.PoisonArrowEffect;
import pl.arieals.minigame.goldhunter.player.AbilityHandler;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

public class PoisonArrowAbility implements AbilityHandler
{
    @Inject
    private static GoldHunter goldHunter;

    @Override
    public boolean onUse(GoldHunterPlayer player, Location targetBlock)
    {
        player.getEffectTracker().addEffect(new PoisonArrowEffect());
        return true;
    }
}
