package pl.north93.northplatform.api.minigame.shared.api.booster.type;

import pl.north93.northplatform.api.minigame.shared.api.booster.IBooster;
import pl.north93.northplatform.api.global.network.players.IPlayer;

public class BigEventBooster implements IBooster
{
    @Override
    public String getId()
    {
        return "bigevent";
    }

    @Override
    public double getMultiplier(final IPlayer player)
    {
        return 0.5;
    }
}
