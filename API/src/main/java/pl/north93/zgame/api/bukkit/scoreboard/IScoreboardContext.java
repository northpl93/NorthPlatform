package pl.north93.zgame.api.bukkit.scoreboard;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.bukkit.entity.Player;

public interface IScoreboardContext
{
    Player getPlayer();

    IScoreboardLayout getLayout();

    void set(String key, Object value);

    void set(Map<String, Object> data);

    <T> T get(String key);

    /**
     * Dodaje podane CompletableFuture do mapy zmiennych.
     * Dodatkowo gdy zostanie zakonczone, zostanie wywolana aktualizacja scoreboardu.
     *
     * @param key Klucz.
     * @param future Wartosc jako completablefuture.
     * @param <T> Typ.
     */
    <T> void setCompletableFuture(String key, CompletableFuture<T> future);

    /**
     * Pobiera wartosc danego CompletableFuture (jesli zostalo zakonczone).
     * W przeciwnym wypadku zwroci pusty Optional.
     *
     * @param key Klucz.
     * @param <T> Typ.
     * @return Opcjonalny wynik z CompletableFuture.
     */
    <T> Optional<T> getCompletableFuture(String key);

    default String getLocale()
    {
        return this.getPlayer().spigot().getLocale();
    }

    void update(); // force update
}
