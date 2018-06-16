package pl.arieals.api.minigame.shared.api.arena;

import java.util.Set;
import java.util.UUID;

import pl.arieals.api.minigame.shared.api.GameIdentity;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.api.minigame.shared.api.status.InGameStatus;
import pl.north93.zgame.api.global.metadata.MetaStore;
import pl.arieals.api.minigame.shared.api.status.IPlayerStatus;

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

    /**
     * Sprawdza czy ta arena hostuje gre dynamiczna.
     * (czyli taka ktora zezwala na wchodzenie w trakcie gry)
     *
     * @return czy arena hostuje gre dynamiczna.
     */
    boolean isDynamic();

    /**
     * Zwraca nazwe aktualnie zaladowanego swiata na arenie.
     *
     * @return nazwa aktualnie zaladowanego swiata.
     */
    String getWorldId();
    
    /**
     * 
     * @return wyswietlana nazwa aktualnie zaladowanego swiata
     */
    String getWorldDisplayName();

    /**
     * Zwraca aktualny stan gry na danej arenie.
     *
     * @return aktualny stan gry na danej arenie.
     */
    GamePhase getGamePhase();

    default int getPlayersCount()
    {
        return this.getPlayers().size();
    }

    Set<UUID> getPlayers();

    /**
     * Zwraca maksymalna ilosc graczy mogacych byc na tej arenie.
     * Jest to wartosc z konfiguracji minigry.
     *
     * @return maksymalna ilosc graczy na arenie.
     */
    int getMaxPlayers();

    /**
     * Zwraca obiekt statusu reprezentujący grę na tej arenie.
     *
     * @return Obiekt statusu tej areny.
     */
    default IPlayerStatus getPlayerStatus()
    {
        return new InGameStatus(this.getServerId(), this.getId(), this.getMiniGame());
    }
    
    /**
     * 
     * @return zwraca obiekt metastore umozliwiajacy przypisanie metadaty do areny
     */
    MetaStore getMetadata();

}
