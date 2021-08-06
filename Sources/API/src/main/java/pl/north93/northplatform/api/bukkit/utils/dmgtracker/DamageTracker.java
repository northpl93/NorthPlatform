package pl.north93.northplatform.api.bukkit.utils.dmgtracker;

import javax.annotation.Nonnull;

import java.time.Instant;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.bukkit.server.IBukkitServerManager;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

/**
 * Narzedzie sluzace do rejestrowania ostatnio otrzymanych
 * obrazen przez gracza. Aby zostalo wlaczone nalezy uzyc
 * metody {@link DamageTracker#get()}.
 *
 * @see DamageContainer
 */
@Slf4j
public class DamageTracker implements Listener
{
    private static DamageTracker tracker;
    @Inject
    private IBukkitServerManager serverManager;

    public static DamageTracker get()
    {
        if (tracker == null)
        {
            tracker = new DamageTracker();
            tracker.serverManager.registerEvents(tracker);

            log.info("Damage tracker is now ready to work");
        }

        return tracker;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(final EntityDamageEvent event)
    {
        if (event.getEntityType() != EntityType.PLAYER)
        {
            return;
        }

        final Player player = (Player) event.getEntity();
        final DamageContainer container = this.getContainer(player);
        container.handleDamage(new DamageEntry(event, Instant.now()));
    }

    public @Nonnull DamageContainer getContainer(final Player player)
    {
        if (player.hasMetadata("damageTracker"))
        {
            return (DamageContainer) player.getMetadata("damageTracker").get(0).value();
        }
        final DamageContainer container = new DamageContainer(player);
        player.setMetadata("damageTracker", this.serverManager.createFixedMetadataValue(container));
        return container;
    }
}
