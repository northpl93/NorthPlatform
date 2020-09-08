package pl.north93.northplatform.api.bukkit.server;

import org.bukkit.event.Listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.north93.northplatform.api.global.component.annotations.bean.Aggregator;

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
    static void aggregate(final IBukkitServerManager serverManager, final AutoListener instance)
    {
        final String className = instance.getClass().getName();
        final Logger log = LoggerFactory.getLogger(AutoListener.class);

        if (! instance.registrationCondition())
        {
            log.info("Skipped registration of class {} as AutoListener.", className);
            return;
        }

        log.debug("Registering {} as Bukkit listener.", className);
        serverManager.registerEvents(instance);
    }
}
