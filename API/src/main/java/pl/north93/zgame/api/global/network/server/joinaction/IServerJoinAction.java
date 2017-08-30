package pl.north93.zgame.api.global.network.server.joinaction;

import pl.north93.zgame.api.bukkit.player.INorthPlayer;

/**
 * Reprezentuje akcje do wywołania po wejściu gracza na serwer.
 */
public interface IServerJoinAction
{
    void playerJoined(INorthPlayer player);
}
