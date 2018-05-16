package pl.arieals.api.minigame.shared.api.statistics;

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
    <UNIT extends IStatisticUnit> CompletableFuture<IRecord<UNIT>> getBest(IStatistic<UNIT> statistic, IStatisticFilter... filters);

    /**
     * Zwraca ostatnią zarejestrowaną wartość danej statystyki w odpowiedniej jednostce.
     *
     * @param statistic Statystyka dla ktorej tworzymy zapytanie.
     * @param filters Filtry zastosowane do szukania.
     * @param <UNIT> Jednostka danej statystyki.
     * @return CompletableFuture z wynikiem w odpowiedniej jednostce.
     */
    <UNIT extends IStatisticUnit> CompletableFuture<IRecord<UNIT>> getLatest(IStatistic<UNIT> statistic, IStatisticFilter... filters);

    /**
     * Rejestruje nowa wartosc statystyki.
     *
     * @param statistic Statystyka dla ktorej rejestrujemy wartosc.
     * @param value Nowa wartosc.
     * @param <UNIT> Jednostka danej statystyki.
     * @return Zwraca poprzednia wartosc.
     */
    <UNIT extends IStatisticUnit> CompletableFuture<IRecord<UNIT>> record(IStatistic<UNIT> statistic, UNIT value);

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
