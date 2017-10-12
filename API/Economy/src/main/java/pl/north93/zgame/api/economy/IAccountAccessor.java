package pl.north93.zgame.api.economy;

import pl.north93.zgame.api.global.network.players.IPlayer;

/**
 * Interfejs udostepniajacy stan konta konkretnego gracza w trybie
 * tylko do odczytu.
 */
public interface IAccountAccessor
{
    /**
     * Zwraca obiekt gracza do ktorego odnosi sie ta instancja.
     *
     * @return Gracz do ktorego odnosi sie ta instancja.
     */
    IPlayer getAssociatedPlayer();

    /**
     * Zwraca walute do ktorej odnosi sie ta instancja.
     *
     * @return Waluta do ktorej odnosi sie ta instancja.
     */
    ICurrency getCurrency();

    /**
     * Zwraca aktualny stan konta danego gracza.
     *
     * @return Aktualny stan konta.
     */
    double getAmount();

    /**
     * Sprawdza czy gracz posiada aktualnie taka ilosc waluty.
     *
     * @param amount Ilosc waluty do sprawdzenia.
     * @return True jesli gracz posiada taka ilosc.
     */
    boolean has(double amount);
}
