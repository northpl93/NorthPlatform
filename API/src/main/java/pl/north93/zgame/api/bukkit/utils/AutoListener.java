package pl.north93.zgame.api.bukkit.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.event.Listener;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Aggregator;

/**
 * Klasa implementujaca ten interfejs zostanie automatycznie
 * zarejestrowana jako listener Bukkita.
 * Opcjonalnie mozna zaimplementowac metode {@link #registrationCondition()},
 * w przypadku gdy zwroci ona false klasa nie zostanie zarejestrowana.
 */
public interface AutoListener extends Listener
{
    /**
     * Metoda warunkujaca automatyczne zarejestrowanie klasy jako
     * listener. Pola klasy podczas wykonywania tej metody beda wstrzykniete,
     * wiec mozna ich bezpiecznie uzyc.
     *
     * @return true w przypadku gdy chcemy rejestrowac klase jako listener.
     */
    default boolean registrationCondition()
    {
        return true;
    }

    @Aggregator(AutoListener.class)
    static void aggregate(final BukkitApiCore apiCore, final AutoListener instance)
    {
        final String className = instance.getClass().getName();
        final Logger log = apiCore.getLogger();

        if (! instance.registrationCondition())
        {
            log.log(Level.FINE, "[AutoListener] Skipped registration of class {0}.", className);
            return;
        }

        log.log(Level.FINE, "[AutoListener] Registering {0} as Bukkit listener.", className);
        apiCore.registerEvents(instance);
    }
}
