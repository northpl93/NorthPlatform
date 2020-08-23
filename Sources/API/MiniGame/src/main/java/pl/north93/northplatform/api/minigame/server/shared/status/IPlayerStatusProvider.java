package pl.north93.northplatform.api.minigame.server.shared.status;

import org.bukkit.entity.Player;

import pl.north93.northplatform.api.minigame.shared.api.status.IPlayerStatus;

public interface IPlayerStatusProvider
{
    /**
     * Zwraca sieciową lokalizację danego gracza znajdującego się lokalnie na serwerze.
     *
     * @param player Lokalny gracz dla którego pobieramy lokalizację.
     * @return Lokalizacja sieciowa danego lokalnego gracza.
     */
    IPlayerStatus getLocation(Player player);
}
