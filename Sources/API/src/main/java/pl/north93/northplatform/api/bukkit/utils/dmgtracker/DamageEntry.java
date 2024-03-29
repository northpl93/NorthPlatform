package pl.north93.northplatform.api.bukkit.utils.dmgtracker;

import javax.annotation.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import com.google.common.base.Preconditions;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Obiekt reprezentujacy otrzymane obrazenia.
 */
public class DamageEntry
{
    private final Instant           time;
    private final EntityDamageEvent cause;
    private final ItemStack         tool;

    public DamageEntry(final EntityDamageEvent cause, final Instant time)
    {
        this.cause = cause;
        this.time = time;
        this.tool = this.initTool();
    }

    private ItemStack initTool()
    {
        final Player playerDamager = this.getPlayerDamager();
        if (playerDamager != null)
        {
            if (this.getCauseByEntity().getDamager().getType() == EntityType.ARROW)
            {
                return new ItemStack(Material.BOW);
            }
            return playerDamager.getInventory().getItemInMainHand();
        }
        return null;
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
     * Zwraca przedmiot ktorym zadano te obrazenia.
     * @return Przedmiot ktorym zadano obrazenia.
     */
    public Optional<ItemStack> getTool()
    {
        return Optional.ofNullable(this.tool);
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

    /**
     * Jesli gracz zadal te obrazenia to zostanie tu zwrocona jego instancja.
     * W przeciwnym wypadku tu bedzie null.
     *
     * @return Gracz ktory zadal te obrazenia lub null jesli nie pochodza od gracza.
     */
    public @Nullable Player getPlayerDamager()
    {
        final EntityDamageEvent.DamageCause cause = this.cause.getCause();
        if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK)
        {
            final EntityDamageByEntityEvent byEntity = (EntityDamageByEntityEvent) this.cause;
            final Entity damager = byEntity.getDamager();
            if (damager.getType() == EntityType.PLAYER)
            {
                return (Player) damager;
            }
        }
        else if (cause == EntityDamageEvent.DamageCause.PROJECTILE)
        {
            final EntityDamageByEntityEvent byEntity = (EntityDamageByEntityEvent) this.cause;
            final Projectile projectileDamager = (Projectile) byEntity.getDamager();

            if (projectileDamager.getShooter() instanceof Player)
            {
                return (Player) projectileDamager.getShooter();
            }
        }

        return null;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("time", this.time).append("cause", this.cause).toString();
    }
}
