package pl.arieals.api.minigame.shared.api.booster.type;

import pl.arieals.api.minigame.shared.api.booster.IBooster;
import pl.north93.zgame.api.global.network.players.IPlayer;
import pl.north93.zgame.api.global.permissions.Group;

public class VipBooster implements IBooster
{
    @Override
    public String getId()
    {
        return "vip";
    }

    @Override
    public long getExpiration(final IPlayer player)
    {
        final long groupExpireAt = player.getGroupExpireAt();
        if (groupExpireAt > 0)
        {
            return groupExpireAt;
        }

        return -1;
    }

    @Override
    public boolean isBoosterValid(final IPlayer player)
    {
        final Group group = player.getGroup();
        return group.hasPermission("booster.vip") || group.hasPermission("booster.svip");
    }

    @Override
    public double getMultiplier(final IPlayer player)
    {
        final Group group = player.getGroup();
        if (group.hasPermission("booster.svip"))
        {
            return 0.25;
        }
        else if (group.hasPermission("booster.vip"))
        {
            return 0.1;
        }

        return 0;
    }
}
