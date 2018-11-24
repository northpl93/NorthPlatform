package pl.north93.northplatform.api.minigame.shared.api.status;

import java.util.UUID;

/**
 * Reprezentuje status gracza w sieci, opisuje aktualną lokalizację.
 */
public interface IPlayerStatus
{
    /**
     * @return ID serwera na którym znajduje się gracz.
     */
    UUID getServerId();

    /**
     * @return Typ statusu.
     */
    StatusType getType();

    boolean equals(Object other);

    int hashCode();

    enum StatusType
    {
        HUB,
        GAME,
        UNKNOWN,
        OFFLINE
    }
}
