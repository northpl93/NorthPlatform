package pl.north93.zgame.api.global.network.mojang;

import java.util.Optional;

public interface IMojangCache
{
    /**
     * Pobiera informacje o profilu powiazanym z danym nickiem.
     * W pierwszej kolejnosci odpytana zostanie lokalna baza danych serwera (redis&mongo).
     * <p>
     * Wielkosc znaków ma znaczenie dla UUID nicku no-premium, nicki premium
     * są sprawdzane niezależnie od podanej wielkości znaków.
     *
     * @param username Nick ktory sprawdzamy.
     * @return Opcjonalnie informacje o danym nicku. W wypadku braku moze to oznaczac
     *         problem z komunikacja z serwerami Mojangu.
     */
    Optional<UsernameDetails> getUsernameDetails(String username);
}
