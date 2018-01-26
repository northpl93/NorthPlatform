package pl.arieals.api.minigame.server;

import java.util.Collection;

import org.bukkit.entity.Player;

import pl.arieals.api.minigame.shared.api.location.INetworkLocation;

public interface IServerManager
{
    void start();

    void stop();

    void tpToHub(Collection<Player> players, String hubId);

    /**
     * Zwraca sieciową lokalizację danego gracza znajdującego się lokalnie na serwerze.
     *
     * @param player Lokalny gracz dla którego pobieramy lokalizację.
     * @return Lokalizacja sieciowa danego lokalnego gracza.
     */
    INetworkLocation getLocation(Player player);
}
