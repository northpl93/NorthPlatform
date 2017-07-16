package pl.north93.zgame.api.bukkit.utils.dmgtracker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Queue;

import com.google.common.collect.EvictingQueue;

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
    private final Player player;
    private final Queue<DamageEntry> entries;

    public DamageContainer(final Player player)
    {
        this.player = player;
        this.entries = EvictingQueue.create(10);
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
     * @param event element do dodania do kolejki.
     */
    public void handleDamage(final EntityDamageEvent event)
    {
        this.entries.add(new DamageEntry(event));
    }

    /**
     * Zwraca wszystkie ostatnio zarejestrowane obrazenia.
     * Ta metoda zwraca mutowalna kolejke z wnetrza klasy.
     * Kolejka NIE jest thread-safe.
     * @return mutowalna kolejka przechowujaca obrazenia.
     */
    public @Nonnull Queue<DamageEntry> getEntries()
    {
        return this.entries;
    }

    /**
     * Zwraca ostatnie obrazenia otrzymane przez gracza.
     * @return ostatnie obrazenia otrzymane od gracza.
     */
    public @Nullable DamageEntry getLastDamageByPlayer()
    {
        for (final DamageEntry next : this.entries)
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
