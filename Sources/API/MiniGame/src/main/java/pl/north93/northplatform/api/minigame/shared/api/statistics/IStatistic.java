package pl.north93.northplatform.api.minigame.shared.api.statistics;

/**
 * Reprezentuje pojedyncza statystyke zbierajaca dane graczy.
 * @param <UNIT> Jednostka zbieranych danych.
 */
public interface IStatistic<UNIT extends IStatisticUnit>
{
    /**
     * @return unikalna nazwa tej statystyki.
     */
    String getId();

    /**
     * Sprawdza ktora wartosc jest lepsza wedlug tej statystyki.
     * Powinno zwrocic true jesli value2 jest lepsze od value1.
     * Powinno zwrocic false jesli valu2 jest rowne lub gorsze od value1.
     *
     * @param value1 Wartosc pierwsza.
     * @param value2 Wartosc druga, porownywana do pierwszej.
     * @return Zgodnie z opisem metody true lub false.
     */
    boolean isBetter(UNIT value1, UNIT value2);

    IStatisticDbComposer<UNIT> getDbComposer();
}
