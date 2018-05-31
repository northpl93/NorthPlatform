package pl.arieals.api.minigame.shared.api.status;

import java.util.UUID;

/**
 * Reprezentuje lokację w sieci w której znajduje się gracz.
 */
public interface IPlayerStatus
{
    /**
     * @return ID serwera na którym znajduje się gracz.
     */
    UUID getServerId();

    /**
     * @return Typ lokalizacji.
     */
    LocationType getType();

    boolean equals(Object other);

    int hashCode();

    enum LocationType
    {
        HUB,
        GAME,
        UNKNOWN,
        OFFLINE
    }
}
