package pl.arieals.api.minigame.server.gamehost.listener;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerStatus;
import static pl.north93.zgame.api.global.utils.JavaUtils.instanceOf;


import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

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

    @EventHandler
    public void disableInteract(final PlayerInteractEvent event)
    {
        final Player player = event.getPlayer();
        this.cancelIfNecessary(event, player);
    }

    @EventHandler
    public void disableDamage(final EntityDamageEvent event)
    {
        final Player player = instanceOf(event.getEntity(), Player.class);
        this.cancelIfNecessary(event, player);
    }

    @EventHandler
    public void blockBreak(final BlockBreakEvent event)
    {
        final Player player = instanceOf(event.getPlayer(), Player.class);
        this.cancelIfNecessary(event, player);
    }

    @EventHandler
    public void blockBreak(final BlockPlaceEvent event)
    {
        final Player player = instanceOf(event.getPlayer(), Player.class);
        this.cancelIfNecessary(event, player);
    }
}
