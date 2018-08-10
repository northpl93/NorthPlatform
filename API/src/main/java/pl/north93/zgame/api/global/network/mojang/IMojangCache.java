package pl.north93.zgame.api.global.network.mojang;

import java.util.Optional;
import java.util.UUID;

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

    /**
     * Pobiera informacje o profilu Mojang/Minecraft o podanym UUID.
     * W pierwszej kolejności zostanie odpytana lokalna baza danych serwera (mongo).
     *
     * @param profileId Identyfikator profilu.
     * @return Opcjonalnie informacje o profilu Mojang/Minecraft.
     */
    Optional<CachedProfile> getProfile(UUID profileId);

    /**
     * Aktualizuje cache profilu w bazie danych serwera.
     *
     * @param profile Nowa wersja profilu do wprowadzenia.
     */
    void updateProfile(CachedProfile profile);
}
