package pl.north93.northplatform.api.minigame.shared.api.statistics;

import java.util.Collection;

/**
 * Reprezentuje wygenerowany ranking danej statystyki.
 */
public interface IRanking<UNIT extends IStatisticUnit>
{
    /**
     * Zwraca ilosc miejsc dla ktorych wygenerowano ranking.
     * W przypadku gdy ilosc graczy jest mniejsza niz zadany
     * rozmiar to zostanie tu zwrocony zadany rozmiar.
     *
     * @see #fetchedSize() Aby sprawdzic faktyczna ilosc pobranych rekordow.
     * @return Ilosc miejsc dla ktorych wygenerowano ranking.
     */
    int size();

    /**
     * Zwraca faktyczna ilosc pobranych rekordow z bazy.
     * Nigdy nie jest wieksze od {@link #size()}.
     * Jest mniejsze w przypadku gdy nie ma wystarczajaco graczy.
     *
     * @return liczba pobranych rekordow z bazy.
     */
    int fetchedSize();

    /**
     * Niemodyfikowalna kolekcja pobranych rekordow z bazy.
     *
     * @see #fetchedSize()
     * @return Pobrane z bazy rekordy.
     */
    Collection<IRecord<UNIT>> getPlaces();

    IRecord<UNIT> getPlace(int place);
}
