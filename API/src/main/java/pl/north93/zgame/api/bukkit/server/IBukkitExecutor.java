package pl.north93.zgame.api.bukkit.server;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IBukkitExecutor
{
    /**
     * Synchronizuje podany runnable do wątku serwera przy użyciu Schedulera.
     *
     * @param runnable do zsynchronizowania.
     */
    void sync(Runnable runnable);

    /**
     * Uruchamia podany kod asynchronicznie na BukkitSchedulerze.
     *
     * @param runnable do uruchomienia asynchronicznie.
     */
    void async(Runnable runnable);

    void syncLater(int ticks, Runnable runnable);

    void asyncLater(int ticks, Runnable runnable);

    /**
     * Wykonuje kod w pierwszym argumencie asynchronicznie,
     * wynik przekazuje do drugiego i wykonuje go synchronicznie.
     * Jeś zostanie zwrócony null częsc synchroniczna sie nie wykona.
     *
     * @param async kod asynchroniczny.
     * @param synced kod zsynchronizowany do serwerra.
     * @param <T> wartość przekazywana z kodu asynchronicznego do synchronicznego.
     */
    <T> void mixed(Supplier<T> async, Consumer<T> synced);

    /**
     * Wykonuje kod synchronicznie co podana ilosc tickow.
     *
     * @param every Co ile tickow wykonywac kod.
     * @param runnable Runnable do zaplanowania.
     */
    void syncTimer(int every, Runnable runnable);

    /**
     * Wykonuje kod asynchronicznie co podaną ilość ticków.
     *
     * @param every Co ile tickow wykonywac kod.
     * @param runnable Runnable do zaplanowania.
     */
    void asyncTimer(int every, Runnable runnable);

    /**
     * Wymusza uruchomienie kodu na glownym watku serwera.
     * Jesli juz w nim jestesmy to po prostu wykona kod.
     *
     * @param runnable Kod do wykonania w glownym watku serwera.
     */
    void inMainThread(Runnable runnable);
}
