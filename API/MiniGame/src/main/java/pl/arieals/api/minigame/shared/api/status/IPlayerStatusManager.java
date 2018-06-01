package pl.arieals.api.minigame.shared.api.status;

import pl.north93.zgame.api.global.network.players.Identity;

/**
 * Interfejs menadżera statusów graczy.
 */
public interface IPlayerStatusManager
{
    /**
     * Zwraca aktualny status gracza o podanym {@link Identity}.
     *
     * @param identity Identity gracza.
     * @return Aktualny status gracza.
     */
    IPlayerStatus getPlayerStatus(Identity identity);

    /**
     * Aktualizuje aktualny status gracza.
     * Jeśli gracz jest offline to nic nie zrobi.
     *
     * @param identity Identity gracza.
     * @param newStatus Nowy status gracza.
     */
    void updatePlayerStatus(Identity identity, IPlayerStatus newStatus);
}
