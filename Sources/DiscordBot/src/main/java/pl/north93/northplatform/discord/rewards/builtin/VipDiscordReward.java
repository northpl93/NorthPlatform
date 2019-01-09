package pl.north93.northplatform.discord.rewards.builtin;

import java.time.Duration;

import pl.north93.northplatform.api.global.network.players.IPlayer;
import pl.north93.northplatform.api.global.network.players.IPlayerTransaction;
import pl.north93.northplatform.api.global.network.players.Identity;

public class VipDiscordReward extends AbstractGroupReward
{
    private static final Duration VIP_TIME = Duration.ofHours(12);

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
}
