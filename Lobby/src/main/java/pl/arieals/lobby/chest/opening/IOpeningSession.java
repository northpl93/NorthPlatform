package pl.arieals.lobby.chest.opening;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.lobby.hub.HubWorld;

public interface IOpeningSession
{
    Player getPlayer();

    HubWorld getHub();

    /**
     * Zwraca ilosc posiadanych przez gracza skrzynek w tym otwieraniu.
     * <p>
     * Ta wartosc jest tylko informacyjna (uzywana do gui), na
     * jej podstawie nie powinna dzialac logika otwierania.
     *
     * @return Ilosc posiadanych przez gracza skrzynek.
     */
    int getChestsAmount();

    /**
     * Zwraca lokacje w ktorej znajduje sie gracz podczas otwierania.
     * Jest ona pobrana z konfiguracji i nie zmienia sie w trakcie otwierania.
     *
     * @return Miejsce w ktorym stoi gracz podczas otwierania.
     */
    Location getPlayerLocation();

    HubOpeningConfig getConfig();
}
