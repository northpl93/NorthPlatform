package pl.north93.northplatform.api.global.network.server.joinaction;

import org.bukkit.Location;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;

/**
 * Reprezentuje akcje do wywołania po wejściu gracza na serwer.
 */
public interface IServerJoinAction
{
    void playerPreSpawn(INorthPlayer player, Location spawn);

    void playerJoined(INorthPlayer player);
}
