package pl.arieals.api.minigame.shared.api.booster;

import pl.north93.zgame.api.global.network.players.IPlayer;

public interface IBooster
{
    String getId();

    double getMultiplier(IPlayer player);
}
