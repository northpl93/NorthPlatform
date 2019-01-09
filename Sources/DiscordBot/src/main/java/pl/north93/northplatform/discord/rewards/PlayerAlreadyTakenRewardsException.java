package pl.north93.northplatform.discord.rewards;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PlayerAlreadyTakenRewardsException extends RuntimeException
{
    private final RewardTakeEntry rewardTakeEntry;

    public PlayerAlreadyTakenRewardsException(final RewardTakeEntry rewardTakeEntry)
    {
        this.rewardTakeEntry = rewardTakeEntry;
    }
}
