package pl.north93.minecraft.discord.rewards.builtin;

import java.time.Duration;

import pl.north93.zgame.api.global.network.players.IPlayer;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.network.players.Identity;

public class SvipDiscordReward extends AbstractGroupReward
{
    private static final Duration SVIP_TIME = Duration.ofDays(7);

    @Override
    public void apply(final Identity identity)
    {
        try (final IPlayerTransaction t = this.playersManager.transaction(identity))
        {
            final IPlayer player = t.getPlayer();

            switch (player.getGroup().getName())
            {
                case "default":
                    this.giveTimedGroup(player, "svip", SVIP_TIME);
                    break;
                case "vip":
                    this.giveTimedGroup(player, "svip", SVIP_TIME);
                    break;
                case "svip":
                    this.extendGroupTime(player, SVIP_TIME);
                    break;
            }
        }
    }
}
