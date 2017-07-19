package pl.arieals.api.minigame.server.gamehost.reward;

import pl.north93.zgame.api.global.network.players.Identity;

public interface IReward
{
    String getId();

    void apply(Identity identity);
}
