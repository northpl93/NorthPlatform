package pl.north93.northplatform.api.minigame.shared.api.statistics;

import java.util.concurrent.CompletableFuture;

/**
 * Reprezentuje obiekt posiadajacy statystyki.
 * Najczesciej sa ta gracze.
 */
public interface IStatisticHolder
{
    /**
     * Unikalny identyfikator posiadacza statystyk.
     * Sklada się niego typ posiadacza i UUID.
     *
     * @return unikalny identyfikator.
     */
    HolderIdentity getIdentity();

    /**
     * Zwraca najlepszą wartość danej statystyki w odpowiedniej jednostce.
     *
     * @param statistic Statystyka dla ktorej tworzymy zapytanie.
     * @param filters Filtry zastosowane do szukania.
     * @param <UNIT> Jednostka danej statystyki.
     * @return CompletableFuture z wynikiem w odpowiedniej jednostce.
     */
    <T, UNIT extends IStatisticUnit<T>> CompletableFuture<IRecord<T, UNIT>> getBest(IStatistic<T, UNIT> statistic, IStatisticFilter... filters);

    /**
     * Zwraca ostatnią zarejestrowaną wartość danej statystyki w odpowiedniej jednostce.
     *
     * @param statistic Statystyka dla ktorej tworzymy zapytanie.
     * @param filters Filtry zastosowane do szukania.
     * @param <UNIT> Jednostka danej statystyki.
     * @return CompletableFuture z wynikiem w odpowiedniej jednostce.
     */
    <T, UNIT extends IStatisticUnit<T>> CompletableFuture<IRecord<T, UNIT>> getLatest(IStatistic<T, UNIT> statistic, IStatisticFilter... filters);

    /**
     * Rejestruje nowa wartosc statystyki.
     *
     * @param statistic Statystyka dla ktorej rejestrujemy wartosc.
     * @param value Nowa wartosc.
     * @param <UNIT> Jednostka danej statystyki.
     * @return Zwraca poprzednia wartosc.
     */
    <T, UNIT extends IStatisticUnit<T>> CompletableFuture<IRecord<T, UNIT>> record(IStatistic<T, UNIT> statistic, UNIT value);

    /**
     * Inkrementuje podaną statystykę o podaną wartość.
     *
     * @param statistic Statystyka dla ktorej inkrementujemy wartosc.
     * @param value O ile inkrementowac statystyke.
     * @param <UNIT> Jednostka danej statystyki.
     * @return Poprzednia wartosc statystyki.
     */
    <T, UNIT extends IStatisticUnit<T>> CompletableFuture<IRecord<T, UNIT>> increment(IStatistic<T, UNIT> statistic, UNIT value);
}
