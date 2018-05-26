package pl.arieals.api.minigame.server;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.entity.Player;

import pl.arieals.api.minigame.shared.api.hub.IHubServer;
import pl.arieals.api.minigame.shared.api.location.INetworkLocation;

public interface IServerManager
{
    void start();

    void stop();

    UUID getServerId();

    void tpToHub(Collection<Player> players, String hubId);

    void tpToHub(Collection<Player> players, IHubServer hubServer, String hubId);

    /**
     * Zwraca sieciową lokalizację danego gracza znajdującego się lokalnie na serwerze.
     *
     * @param player Lokalny gracz dla którego pobieramy lokalizację.
     * @return Lokalizacja sieciowa danego lokalnego gracza.
     */
    INetworkLocation getLocation(Player player);
}
