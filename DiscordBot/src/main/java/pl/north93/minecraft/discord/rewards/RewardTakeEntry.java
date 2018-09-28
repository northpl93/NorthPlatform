package pl.north93.minecraft.discord.rewards;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RewardTakeEntry
{
    private String  discordId;
    private UUID    userId;
    private Instant taken;
}
