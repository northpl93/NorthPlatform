package pl.north93.northplatform.discord.rewards.builtin;

import static java.time.Instant.ofEpochMilli;


import java.time.Duration;
import java.time.Instant;

import pl.north93.northplatform.discord.rewards.IDiscordReward;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.players.IPlayer;
import pl.north93.northplatform.api.global.network.players.IPlayersManager;
import pl.north93.northplatform.api.global.permissions.Group;
import pl.north93.northplatform.api.global.permissions.PermissionsManager;

public abstract class AbstractGroupReward implements IDiscordReward
{
    @Inject
    protected IPlayersManager    playersManager;
    @Inject
    protected PermissionsManager permissionsManager;

    protected final void giveTimedGroup(final IPlayer player, final String groupName, final Duration time)
    {
        final Group group = this.permissionsManager.getGroupByName(groupName);
        player.setGroup(group);

        final Instant expireTime = Instant.now().plus(time);
        player.setGroupExpireAt(expireTime.toEpochMilli());
    }

    protected final void extendGroupTime(final IPlayer player, final Duration time)
    {
        if (player.isGroupExpired())
        {
            final Instant expireTime = Instant.now().plus(time);
            player.setGroupExpireAt(expireTime.toEpochMilli());
        }
        else
        {
            final Instant currentExpiration = ofEpochMilli(player.getGroupExpireAt());
            final Instant newExpiration = currentExpiration.plus(time);

            player.setGroupExpireAt(newExpiration.toEpochMilli());
        }
    }
}
