package pl.north93.zgame.api.bukkit.utils.dmgtracker;

import java.time.Duration;
import java.time.Instant;

import com.google.common.base.Preconditions;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Obiekt reprezentujacy otrzymane obrazenia.
 */
public class DamageEntry
{
    private final Instant           time;
    private final EntityDamageEvent cause;

    public DamageEntry(final EntityDamageEvent cause)
    {
        this.cause = cause;
        this.time = Instant.now();
    }

    /**
     * Zwraca czas otrzymania obrazen.
     * @return czas otrzymania obrazen.
     */
    public Instant getTime()
    {
        return this.time;
    }

    /**
     * Sprawdza czy dane obrazenia nie sa starsze niz dane Duration.
     * <p>
     * np. {@code Duration.ofSeconds(10)} zwroci true w przypadku
     * gdy obrazenia zostaly otrzymane w ciagu ostatnich 10 sekund.
     * @param duration okres do porownania.
     * @return true jesli obrazenia NIE sa starsze niz duration.
     */
    public boolean isNotOlder(final Duration duration)
    {
        final Duration between = Duration.between(this.time, Instant.now());
        return duration.compareTo(between) > 0;
    }

    /**
     * Zwraca obiekt eventu reprezentujacy te obrazenia.
     * @return event obrazen.
     */
    public EntityDamageEvent getCause()
    {
        return this.cause;
    }

    /**
     * Zwraca obiekt eventu reprezentujący obrażenia zcastowany na
     * {@link EntityDamageByEntityEvent}.
     * @return event obrazen.
     */
    public EntityDamageByEntityEvent getCauseByEntity()
    {
        return (EntityDamageByEntityEvent) this.cause;
    }

    /**
     * Sprawdza czy inny damage entry zawiera obrazenia podobnego typu.
     * Sprawdzany jest typ, jesli jest to atak przez entity to porownywane
     * jest entity.
     * @param entry damage entry do porownania.
     * @return czy inny obiekt zawiera podobne obrazenia.
     */
    public boolean isSimilar(final DamageEntry entry)
    {
        Preconditions.checkNotNull(entry);

        final EntityDamageEvent other = entry.getCause();
        if (other.getCause() != this.cause.getCause())
        {
            return false;
        }

        if (this.cause instanceof EntityDamageByEntityEvent)
        {
            if (! (other instanceof EntityDamageByEntityEvent))
            {
                return false;
            }

            final EntityDamageByEntityEvent byEntityOur = (EntityDamageByEntityEvent) this.cause;
            final EntityDamageByEntityEvent byEntityOther = (EntityDamageByEntityEvent) other;

            if (byEntityOur.getDamager() != byEntityOther.getDamager())
            {
                return false;
            }
        }

        return true;
    }
}
