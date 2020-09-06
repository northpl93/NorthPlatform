package pl.north93.northplatform.itemshop.controller.handlers;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.players.IPlayersManager;
import pl.north93.northplatform.api.global.network.players.Identity;
import pl.north93.northplatform.api.minigame.shared.api.booster.type.ShopBooster;
import pl.north93.northplatform.itemshop.shared.IDataHandler;

public class BoosterDataHandler implements IDataHandler
{
    @Inject
    private IPlayersManager playersManager;

    @Override
    public String getId()
    {
        return "booster";
    }

    @Override
    public boolean process(final Identity player, final Map<String, String> data)
    {
        final int time = Integer.parseInt(data.get("time")); // w sekundach

        return this.playersManager.access(player, playerObj ->
        {
            ShopBooster.extendBoost(playerObj, TimeUnit.SECONDS.toMillis(time));
        });
    }
}
