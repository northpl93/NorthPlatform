package pl.north93.minecraft.discord.rewards.builtin;

import static java.time.Instant.ofEpochMilli;


import java.time.Duration;
import java.time.Instant;

import pl.north93.minecraft.discord.rewards.IDiscordReward;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.players.IPlayer;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.network.players.IPlayersManager;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.permissions.Group;
import pl.north93.zgame.api.global.permissions.PermissionsManager;

public class VipDiscordReward implements IDiscordReward
{
    private static final Duration VIP_TIME = Duration.ofHours(12);
    @Inject
    private IPlayersManager    playersManager;
    @Inject
    private PermissionsManager permissionsManager;

    @Override
    public void apply(final Identity identity)
    {
        try (final IPlayerTransaction t = this.playersManager.transaction(identity))
        {
            final IPlayer player = t.getPlayer();

            switch (player.getGroup().getName())
            {
                case "default":
                    this.giveTimedGroup(player, "vip", VIP_TIME);
                    break;
                case "vip":
                    this.extendGroupTime(player, VIP_TIME);
                    break;
                case "svip":
                    this.extendGroupTime(player, VIP_TIME);
                    break;
            }
        }
    }

    private void giveTimedGroup(final IPlayer player, final String groupName, final Duration time)
    {
        final Group group = this.permissionsManager.getGroupByName(groupName);
        player.setGroup(group);

        final Instant expireTime = Instant.now().plus(time);
        player.setGroupExpireAt(expireTime.toEpochMilli());
    }

    private void extendGroupTime(final IPlayer player, final Duration time)
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
