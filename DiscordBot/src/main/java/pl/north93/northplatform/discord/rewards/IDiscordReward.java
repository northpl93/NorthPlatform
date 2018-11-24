package pl.north93.northplatform.discord.rewards;

import pl.north93.northplatform.api.global.network.players.Identity;

public interface IDiscordReward
{
    void apply(Identity identity);
}
