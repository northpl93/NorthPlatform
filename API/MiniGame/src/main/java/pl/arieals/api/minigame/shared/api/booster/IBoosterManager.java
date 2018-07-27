package pl.arieals.api.minigame.shared.api.booster;

import java.util.Collection;

import pl.north93.zgame.api.global.network.players.IPlayer;

public interface IBoosterManager
{
    Collection<IBooster> getBoosters();

    Collection<IBooster> getEnabledBoosters();

    boolean isBoosterEnabled(IBooster booster);

    Collection<IBooster> getValidBoosters(IPlayer player);

    double calculateFinalMultiplier(IPlayer player);
}
