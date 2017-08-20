package pl.arieals.api.minigame.shared.api.statistics;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Reprezentuje obiekt posiadajacy statystyki.
 * Najczesciej sa ta gracze.
 */
public interface IStatisticHolder
{
    /**
     * Unikalny identyfikator posiadacza statystyki.
     *
     * @return unikalny identyfikator.
     */
    UUID getUniqueId();

    /**
     * Zwraca wartosc danej statystyki w odpowiedniej jednostce.
     *
     * @param statistic Statystyka dla ktorej tworzymy zapytanie.
     * @param <UNIT> Jednostka danej statystyki.
     * @return CompletableFuture z wynikiem w odpowiedniej jednostce.
     */
    <UNIT extends IStatisticUnit> CompletableFuture<IRecord<UNIT>> getValue(IStatistic<UNIT> statistic);

    /**
     * Rejestruje nowa wartosc statystyki.
     *
     * @param statistic Statystyka dla ktorej rejestrujemy wartosc.
     * @param value Nowa wartosc.
     * @param onlyWhenBetter Jesli tu bedzie true podmiana nastapi tylko wtedy gdy
     *                       nowa wartosc bedzie lepsza od starej.
     * @param <UNIT> Jednostka danej statystyki.
     * @return Zwraca poprzednia wartosc.
     */
    <UNIT extends IStatisticUnit> CompletableFuture<IRecord<UNIT>> record(IStatistic<UNIT> statistic, UNIT value, boolean onlyWhenBetter);

    default <UNIT extends IStatisticUnit> CompletableFuture<IRecord<UNIT>> record(IStatistic<UNIT> statistic, UNIT value)
    {
        return this.record(statistic, value, false);
    }

    /**
     * Inkrementuje podaną statystykę o podaną wartość.
     *
     * @param statistic Statystyka dla ktorej inkrementujemy wartosc.
     * @param value O ile inkrementowac statystyke.
     * @param <UNIT> Jednostka danej statystyki.
     * @return Poprzednia wartosc statystyki.
     */
    <UNIT extends IStatisticUnit> CompletableFuture<IRecord<UNIT>> increment(IStatistic<UNIT> statistic, UNIT value);
}
