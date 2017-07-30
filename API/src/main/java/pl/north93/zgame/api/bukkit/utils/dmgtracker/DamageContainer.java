package pl.north93.zgame.api.bukkit.utils.dmgtracker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Queue;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Obiekt powiazany z danym graczem. Przechowuje historie
 * otrzymanych przez danego gracza obrazen.
 */
public class DamageContainer
{
    private final Player                     player;
    private final EvictingDeque<DamageEntry> entries;

    public DamageContainer(final Player player)
    {
        this.player = player;
        this.entries = new EvictingDeque<>(10);
    }

    /**
     * Zwraca gracza powiazanego z danym kontenerem.
     * @return gracz powiazany z tym kontenerem.
     */
    public @Nonnull Player getPlayer()
    {
        return this.player;
    }

    /**
     * Dodaje nowa pozycje z obrazeniem otrzymanym przez gracza.
     * Metoda nie jest thread-safe.
     * @param damageEntry element do dodania do kolejki.
     */
    public void handleDamage(final DamageEntry damageEntry)
    {
        this.entries.add(damageEntry);
    }

    /**
     * Zwraca wszystkie ostatnio zarejestrowane obrazenia.
     * Pierwszym elementem jest najnowsze otrzymane obrazenie.
     * @return kopia kolejki z otrzymanymi obrazeniami.
     */
    public @Nonnull Deque<DamageEntry> getEntries()
    {
        final Deque<DamageEntry> queue = new ArrayDeque<>(10);
        final Iterator<DamageEntry> iterator = this.entries.descendingIterator();
        while (iterator.hasNext())
        {
            queue.add(iterator.next());
        }

        return queue;
    }

    /**
     * Zwraca zarejestrowane obrazenia nie starsze niz
     * podany duration. Ta metoda zwraca skopiowana
     * kolejke.
     * @param duration limit czasu.
     * @return kolejka z obrazeniami nie starszymi niz duration.
     */
    public @Nonnull Deque<DamageEntry> getEntriesNotOlder(final Duration duration)
    {
        final Deque<DamageEntry> queue = new ArrayDeque<>(10);

        final Iterator<DamageEntry> iterator = this.entries.descendingIterator();
        while (iterator.hasNext())
        {
            final DamageEntry entry = iterator.next();
            if (entry.isNotOlder(duration))
            {
                queue.add(entry);
                continue;
            }
            break;
        }

        return queue;
    }

    /**
     * Zwraca ostatnie obrazenia otrzymane przez gracza.
     * @return ostatnie obrazenia otrzymane od gracza.
     */
    public @Nullable DamageEntry getLastDamageByPlayer()
    {
        return this.getLastDamageByPlayer0(this.getEntries());
    }

    /**
     * Zwraca ostatnie obrazenia otrzymane przez gracza
     * nie starsze niz podany limit czasowy.
     * @param duration limit czasu.
     * @return ostatnie obrazenia od gracza.
     */
    public @Nullable DamageEntry getLastDamageByPlayer(final Duration duration)
    {
        return this.getLastDamageByPlayer0(this.getEntriesNotOlder(duration));
    }

    private @Nullable DamageEntry getLastDamageByPlayer0(final Queue<DamageEntry> entries)
    {
        for (final DamageEntry next : entries)
        {
            final EntityDamageEvent cause = next.getCause();
            if (cause.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK)
            {
                final EntityDamageByEntityEvent byEntity = (EntityDamageByEntityEvent) cause;
                final Entity damager = byEntity.getDamager();
                if (damager.getType() != EntityType.PLAYER)
                {
                    continue;
                }

                return next;
            }
        }

        return null;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("player", this.player).append("entries", this.entries).toString();
    }
}
