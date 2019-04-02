package pl.north93.northplatform.api.bukkit.utils.dmgtracker;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Disabled;

@Disabled
public class DamageTrackerTest
{
//    @Test
//    public void notOlderTest()
//    {
//        final DamageContainer container = new DamageContainer(null);
//
//        final EntityDamageEvent cause = PowerMockito.mock(EntityDamageEvent.class);
//        when(cause.getCause()).thenReturn(CUSTOM);
//
//        container.handleDamage(new DamageEntry(cause, this.xSecondsBefore(9)));
//        container.handleDamage(new DamageEntry(cause, this.xSecondsBefore(6)));
//
//        container.handleDamage(new DamageEntry(cause, this.xSecondsBefore(4)));
//        container.handleDamage(new DamageEntry(cause, this.xSecondsBefore(2)));
//
//        assertEquals(0, container.getEntriesNotOlder(Duration.ofSeconds(1)).size());
//        assertEquals(2, container.getEntriesNotOlder(Duration.ofSeconds(5)).size());
//        assertEquals(4, container.getEntriesNotOlder(Duration.ofSeconds(10)).size());
//    }
//
//    @Test
//    public void lastDamageByPlayerTest()
//    {
//        final DamageContainer container = new DamageContainer(null);
//
//        final CraftPlayer mockedPlayer1 = PowerMockito.mock(CraftPlayer.class);
//        when(mockedPlayer1.getType()).thenCallRealMethod();
//        when(mockedPlayer1.getInventory()).thenReturn(PowerMockito.mock(PlayerInventory.class));
//        final EntityDamageByEntityEvent mockedEvent1 = PowerMockito.mock(EntityDamageByEntityEvent.class);
//        when(mockedEvent1.getCause()).thenReturn(ENTITY_ATTACK);
//        when(mockedEvent1.getDamager()).thenReturn(mockedPlayer1);
//
//        final CraftPlayer mockedPlayer2 = PowerMockito.mock(CraftPlayer.class);
//        when(mockedPlayer2.getType()).thenCallRealMethod();
//        when(mockedPlayer2.getInventory()).thenReturn(PowerMockito.mock(PlayerInventory.class));
//        final EntityDamageByEntityEvent mockedEvent2 = PowerMockito.mock(EntityDamageByEntityEvent.class);
//        when(mockedEvent2.getCause()).thenReturn(ENTITY_ATTACK);
//        when(mockedEvent2.getDamager()).thenReturn(mockedPlayer2);
//
//        container.handleDamage(new DamageEntry(mockedEvent1, this.xSecondsBefore(9)));
//        container.handleDamage(new DamageEntry(mockedEvent2, this.xSecondsBefore(6)));
//
//        {
//            // zachowanie metody gdy nie podajemy limitu czasu
//            final DamageEntry lastDamageByPlayer = container.getLastDamageByPlayer();
//            assertNotNull(lastDamageByPlayer);
//            assertEquals(mockedPlayer2, lastDamageByPlayer.getCauseByEntity().getDamager());
//        }
//
//        {
//            // zachowanie metody gdy podajemy limit czasu
//            final DamageEntry lastDamageByPlayer = container.getLastDamageByPlayer(Duration.ofSeconds(10));
//            assertNotNull(lastDamageByPlayer);
//            assertEquals(mockedPlayer2, lastDamageByPlayer.getCauseByEntity().getDamager());
//        }
//    }

    private Instant xSecondsBefore(final int seconds)
    {
        return Instant.now().minus(seconds, ChronoUnit.SECONDS);
    }
}
