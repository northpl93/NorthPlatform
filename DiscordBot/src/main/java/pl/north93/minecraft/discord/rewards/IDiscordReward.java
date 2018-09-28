package pl.north93.minecraft.discord.rewards;

import pl.north93.zgame.api.global.network.players.Identity;

public interface IDiscordReward
{
    void apply(Identity identity);
}
