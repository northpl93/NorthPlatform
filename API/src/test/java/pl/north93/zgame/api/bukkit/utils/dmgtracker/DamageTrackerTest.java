package pl.north93.zgame.api.bukkit.utils.dmgtracker;

import static org.junit.Assert.assertEquals;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.CUSTOM;


import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Queue;

import com.google.common.base.Function;

import org.bukkit.event.entity.EntityDamageEvent;

import org.junit.Test;

public class DamageTrackerTest
{
    @Test
    public void notOlderTest()
    {
        final DamageContainer container = new DamageContainer(null);

        final HashMap<EntityDamageEvent.DamageModifier, Double> modifiers = new HashMap<>();
        modifiers.put(EntityDamageEvent.DamageModifier.BASE, 0d);
        final HashMap<EntityDamageEvent.DamageModifier, Function<? super Double, Double>> modifierFunctions = new HashMap<>();
        modifierFunctions.put(EntityDamageEvent.DamageModifier.BASE, (d) -> 0d);
        final EntityDamageEvent cause = new EntityDamageEvent(null, CUSTOM, modifiers, modifierFunctions);

        final Queue<DamageEntry> entries = container.getEntries();
        entries.add(new DamageEntry(cause, this.xSecondsBefore(9)));
        entries.add(new DamageEntry(cause, this.xSecondsBefore(6)));

        entries.add(new DamageEntry(cause, this.xSecondsBefore(4)));
        entries.add(new DamageEntry(cause, this.xSecondsBefore(2)));

        assertEquals(0, container.getEntriesNotOlder(Duration.ofSeconds(1)).size());
        assertEquals(2, container.getEntriesNotOlder(Duration.ofSeconds(5)).size());
        assertEquals(4, container.getEntriesNotOlder(Duration.ofSeconds(10)).size());
    }

    private Instant xSecondsBefore(final int seconds)
    {
        return Instant.now().minus(seconds, ChronoUnit.SECONDS);
    }
}
