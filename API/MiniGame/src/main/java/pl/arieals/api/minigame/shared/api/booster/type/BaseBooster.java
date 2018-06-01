package pl.arieals.api.minigame.shared.api.booster.type;

import pl.arieals.api.minigame.shared.api.booster.IBooster;
import pl.north93.zgame.api.global.network.players.IPlayer;

public class BaseBooster implements IBooster
{
    @Override
    public String getId()
    {
        return "base";
    }

    @Override
    public double getMultiplier(final IPlayer player)
    {
        return 1;
    }
}
