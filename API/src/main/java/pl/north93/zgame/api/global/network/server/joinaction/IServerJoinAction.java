package pl.north93.zgame.api.global.network.server.joinaction;

import org.bukkit.entity.Player;

/**
 * Reprezentuje akcje do wywołania po wejściu gracza na serwer.
 */
public interface IServerJoinAction
{
    void playerJoined(Player bukkitPlayer);
}
