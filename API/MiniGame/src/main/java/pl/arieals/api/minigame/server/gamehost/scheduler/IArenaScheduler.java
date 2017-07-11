package pl.arieals.api.minigame.server.gamehost.scheduler;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.zgame.api.bukkit.utils.AbstractCountdown;
import pl.north93.zgame.api.bukkit.utils.SimpleCountdown;

/**
 * Reprezentuje schedulera powiązanego z czasem areny.
 */
public interface IArenaScheduler
{
    /**
     * Zwraca arenę powiązaną z danym schedulerem.
     * @return powiązana arena.
     */
    LocalArena getArena();

    /**
     * Uruchamia dany Runnable po danej ilości ticków.
     * <p>
     * Uwzględniany jest czas areny, w wypadku jego zmiany (np. komendą)
     * zadanie zostanie zaplanowane ponownie.
     *
     * @param task zadanie do uruchomienia.
     * @param delay opoznienie w tickach.
     */
    void runTaskLater(Runnable task, long delay);

    /**
     * Uruchamia dany Runnable co daną ilość ticków z podanym opoznieniem.
     * <p>
     * Uwzglednia czas areny, w wypadku jego zmiany zadanie moze zostac
     * uruchomione kilkukrotnie w zaleznosci od tego ile razy faktycznie
     * byloby uruchomione w danym przedziale czasu.
     *
     * @param task zadanie do uruchomienia.
     * @param delay opoznienie w tickach.
     * @param every co ile odpalac task (w tickach).
     */
    void runTaskTimer(Runnable task, long delay, long every);

    /**
     * Uruchamia dany {@link AbstractCountdown} co dana ilosc tickow.
     * <p>
     * Uwzglednia czas areny, w wypadku jego zmiany zadanie moze zostac
     * uruchomione kilkukrotnie w zaleznosci od tego ile razy faktycznie
     * byloby uruchomione w danym przedziale czasu.
     *
     * @param countdown countdown do uruchomienia.
     * @param every co ile uruchamiac petle countdownu.
     */
    void runAbstractCountdown(AbstractCountdown countdown, long every);

    /**
     * Uruchamia dany {@link SimpleCountdown}.
     * <p>
     * Uwzglednia czas areny, w wypadku jego zmiany zadanie moze zostac
     * uruchomione kilkukrotnie w zaleznosci od tego ile razy faktycznie
     * byloby uruchomione w danym przedziale czasu.
     *
     * @param countdown countdown do uruchomienia.
     */
    void runSimpleCountdown(SimpleCountdown countdown);

    /**
     * Anuluje i kasuje wszystkie zadania z tego schedulera.
     */
    void cancelAndClear();
}
