package pl.arieals.api.minigame.server.gamehost.scheduler;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;

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
     *
     * Uwzględniany jest czas areny, w wypadku jego zmiany (np. komendą)
     * zadanie zostanie zaplanowane ponownie.
     *
     * @param task zadanie do uruchomienia.
     * @param delay opoznienie w tickach.
     */
    void runTaskLater(Runnable task, long delay);

    /**
     * Uruchamia dany Runnable co daną ilość ticków z podanym opoznieniem.
     *
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
     * Anuluje i kasuje wszystkie zadania z tego schedulera.
     */
    void cancelAndClear();
}
