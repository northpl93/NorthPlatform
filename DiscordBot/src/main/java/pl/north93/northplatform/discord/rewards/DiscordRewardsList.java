package pl.north93.northplatform.discord.rewards;

import java.util.ArrayList;
import java.util.List;

import lombok.ToString;
import pl.north93.northplatform.api.global.network.players.Identity;

@ToString
public class DiscordRewardsList
{
    private final List<IDiscordReward> rewards = new ArrayList<>();

    public void addReward(final IDiscordReward reward)
    {
        this.rewards.add(reward);
    }

    public void apply(final Identity identity)
    {
        for (final IDiscordReward reward : this.rewards)
        {
            reward.apply(identity);
        }
    }
}
