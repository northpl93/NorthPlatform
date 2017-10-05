package pl.arieals.lobby.chest.opening;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.lobby.hub.HubWorld;

public interface IOpeningSession
{
    Player getPlayer();

    HubWorld getHub();

    /**
     * Zwraca lokacje w ktorej znajduje sie gracz podczas otwierania.
     * Jest ona pobrana z konfiguracji i nie zmienia sie w trakcie otwierania.
     *
     * @return Miejsce w ktorym stoi gracz podczas otwierania.
     */
    Location getPlayerLocation();

    HubOpeningConfig getConfig();

    // todo typ skrzynki
}
