package pl.arieals.api.minigame.server.gamehost.listener;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerStatus;
import static pl.north93.zgame.api.global.utils.lang.JavaUtils.instanceOf;


import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.util.Vector;

import pl.arieals.api.minigame.server.gamehost.event.player.SpectatorModeChangeEvent;
import pl.arieals.api.minigame.shared.api.PlayerStatus;

/**
 * Nasluchujemy na uruchomienie i wylaczenie trybu spectatora.
 * Nasluchujemy na rzeczy ktorych spectator nie powinien
 * robic.
 */
public class SpectatorListener implements Listener
{
    @EventHandler
    public void onSpectatorEnable(final SpectatorModeChangeEvent event)
    {
        if (! event.getNewStatus().isSpectator())
        {
            return;
        }

        final Player player = event.getPlayer();

        player.setVisible(false);
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    @EventHandler
    public void onSpectatorDisable(final SpectatorModeChangeEvent event)
    {
        if (event.getOldStatus() == null || !event.getOldStatus().isSpectator())
        {
            return;
        }

        final Player player = event.getPlayer();

        player.setVisible(true);
        player.setAllowFlight(false);
        player.setFlying(false);
    }

    // = = = BLOKOWANIE ZLYCH RZECZY SPECTATOROM = = = //

    private void cancelIfNecessary(final Cancellable cancellable, final Player player)
    {
        if (player == null)
        {
            return;
        }

        final PlayerStatus playerStatus = getPlayerStatus(player);
        if (playerStatus == null)
        {
            return;
        }
        if (playerStatus.isSpectator())
        {
            cancellable.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void disableInteract(final PlayerInteractEvent event)
    {
        final Player player = event.getPlayer();
        this.cancelIfNecessary(event, player);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void disableDamage(final EntityDamageEvent event)
    {
        final Player player = instanceOf(event.getEntity(), Player.class);
        if (event instanceof EntityDamageByEntityEvent)
        {
            final EntityDamageByEntityEvent byEntity = (EntityDamageByEntityEvent) event;
            final Player attacker = instanceOf(byEntity.getDamager(), Player.class);
            final Player receiver = instanceOf(byEntity.getDamager(), Player.class);

            this.cancelIfNecessary(event, attacker);
            this.cancelIfNecessary(event, receiver);
        }
        else
        {
            this.cancelIfNecessary(event, player);
        }

        if (player != null && event.getCause() == EntityDamageEvent.DamageCause.VOID && event.isCancelled())
        {
            // ladnie odbijamy gracza w gore jesli dostal damage od voidu
            player.setVelocity(new Vector(0, 4, 0));
        }
    }

    @EventHandler
    public void disableBowShoot(final EntityShootBowEvent event)
    {
        final Player player = instanceOf(event.getEntity(), Player.class);
        this.cancelIfNecessary(event, player);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void blockBreak(final BlockBreakEvent event)
    {
        final Player player = instanceOf(event.getPlayer(), Player.class);
        this.cancelIfNecessary(event, player);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void blockPlace(final BlockPlaceEvent event)
    {
        final Player player = instanceOf(event.getPlayer(), Player.class);
        this.cancelIfNecessary(event, player);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void disableItemPickup(final PlayerPickupItemEvent event)
    {
        final Player player = event.getPlayer();
        this.cancelIfNecessary(event, player);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void disableItemDrop(final PlayerDropItemEvent event)
    {
        final Player player = event.getPlayer();
        this.cancelIfNecessary(event, player);
    }
}
