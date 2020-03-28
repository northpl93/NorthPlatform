package pl.north93.northplatform.minigame.goldhunter.abilities;

import org.bukkit.Location;

import pl.north93.northplatform.minigame.goldhunter.GoldHunter;
import pl.north93.northplatform.minigame.goldhunter.effect.PoisonArrowEffect;
import pl.north93.northplatform.minigame.goldhunter.player.AbilityHandler;
import pl.north93.northplatform.minigame.goldhunter.player.GoldHunterPlayer;
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
