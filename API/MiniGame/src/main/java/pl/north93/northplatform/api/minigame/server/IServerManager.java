package pl.north93.northplatform.api.minigame.server;

import java.util.UUID;

import org.bukkit.entity.Player;

import pl.north93.northplatform.api.minigame.shared.api.hub.IHubServer;
import pl.north93.northplatform.api.minigame.shared.api.status.IPlayerStatus;

public interface IServerManager
{
    void start();

    void stop();

    UUID getServerId();

    void tpToHub(Iterable<? extends Player> players, String hubId);

    void tpToHub(Iterable<? extends Player> players, IHubServer hubServer, String hubId);

    /**
     * Zwraca sieciową lokalizację danego gracza znajdującego się lokalnie na serwerze.
     *
     * @param player Lokalny gracz dla którego pobieramy lokalizację.
     * @return Lokalizacja sieciowa danego lokalnego gracza.
     */
    IPlayerStatus getLocation(Player player);
}
