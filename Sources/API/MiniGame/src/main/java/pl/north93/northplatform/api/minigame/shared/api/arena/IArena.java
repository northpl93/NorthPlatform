package pl.north93.northplatform.api.minigame.shared.api.arena;

import java.util.Set;
import java.util.UUID;

import pl.north93.northplatform.api.minigame.shared.api.GameIdentity;
import pl.north93.northplatform.api.minigame.shared.api.GamePhase;
import pl.north93.northplatform.api.minigame.shared.api.status.InGameStatus;
import pl.north93.northplatform.api.global.metadata.MetaStore;
import pl.north93.northplatform.api.minigame.shared.api.status.IPlayerStatus;

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
     * Zwraca nazwe aktualnie zaladowanego swiata na arenie.
     *
     * @return nazwa aktualnie zaladowanego swiata.
     */
    default String getWorldId()
    {
        return this.getMetadata().get(StandardArenaMetaData.WORLD_ID);
    }

    /**
     *
     * @return wyswietlana nazwa aktualnie zaladowanego swiata
     */
    default String getWorldDisplayName()
    {
        return this.getMetadata().get(StandardArenaMetaData.WORLD_NAME);
    }

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
