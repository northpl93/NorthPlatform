package pl.arieals.api.minigame.shared.api.status;

import pl.north93.zgame.api.global.network.players.Identity;

public interface IPlayerStatusManager
{
    IPlayerStatus getPlayerStatus(Identity identity);

    void updatePlayerStatus(Identity identity, IPlayerStatus newStatus);
}
