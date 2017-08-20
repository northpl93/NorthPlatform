package pl.north93.zgame.api.bukkit.utils.dmgtracker;

import static pl.north93.zgame.api.global.utils.JavaUtils.instanceOf;


import javax.annotation.Nonnull;

import java.time.Instant;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

/**
 * Narzedzie sluzace do rejestrowania ostatnio otrzymanych
 * obrazen przez gracza. Aby zostalo wlaczone nalezy uzyc
 * metody {@link DamageTracker#get()}.
 *
 * @see DamageContainer
 */
public class DamageTracker implements Listener
{
    private static DamageTracker tracker;
    @Inject
    private        BukkitApiCore apiCore;

    public static DamageTracker get()
    {
        if (tracker == null)
        {
            tracker = new DamageTracker();
            tracker.apiCore.registerEvents(tracker);
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

        if (event instanceof EntityDamageByEntityEvent)
        {
            final EntityDamageByEntityEvent byEntity = (EntityDamageByEntityEvent) event;
            final Player damager = instanceOf(byEntity.getDamager(), Player.class);
            if (damager != null)
            {
                container.handleDamage(new DamageEntry(event, Instant.now(), damager.getInventory().getItemInMainHand()));
                return;
            }
        }

        container.handleDamage(new DamageEntry(event, Instant.now()));
    }

    public @Nonnull DamageContainer getContainer(final Player player)
    {
        if (player.hasMetadata("damageTracker"))
        {
            return (DamageContainer) player.getMetadata("damageTracker").get(0).value();
        }
        final DamageContainer container = new DamageContainer(player);
        player.setMetadata("damageTracker", new FixedMetadataValue(this.apiCore.getPluginMain(), container));
        return container;
    }
}
