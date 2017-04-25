package pl.arieals.api.minigame.server.gamehost.lobby;

import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;

public interface ILobbyManager
{
    /**
     * Przenosi gracza do poczekalni dla danej areny.
     *
     * @param arena arena z ktora powiazany jest gracz.
     * @param player gracz.
     */
    void addPlayer(LocalArena arena, Player player);

    /**
     * Usuwa gracza z lobby, ale nigdzie nie teleportuje.
     * @param player gracz
     */
    void removePlayer(Player player);
}
