package pl.arieals.api.minigame.shared.api.arena;

import java.util.Set;
import java.util.UUID;

import pl.arieals.api.minigame.shared.api.GameIdentity;
import pl.arieals.api.minigame.shared.api.GamePhase;

public interface IArena
{
    /**
     * Zwraca unikalny identyfikator tej areny.
     * Unikalny w calej sieci.
     *
     * @return identyfikator tej areny.
     */
    UUID getId();

    /**
     * Zwraca identyfikator serwera hostujacego gre.
     * Jeden serwer moze hostowac wiele aren.
     *
     * @return identyfikator serwera hostujacego ta arene.
     */
    UUID getServerId();

    /**
     * Zwraca identyfikator minigry hostowanej na tej arenie.
     * (minigre i jej wariant).
     *
     * @return identyfikator hostowanej minigry.
     */
    GameIdentity getMiniGame();

    String getWorldId();

    GamePhase getGamePhase();

    default int getPlayersCount()
    {
        return this.getPlayers().size();
    }

    Set<UUID> getPlayers();
}
