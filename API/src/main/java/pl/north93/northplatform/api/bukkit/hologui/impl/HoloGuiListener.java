package pl.north93.northplatform.api.bukkit.hologui.impl;

import static org.bukkit.event.block.Action.LEFT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;


import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class HoloGuiListener implements Listener
{
    private final HoloGuiManagerImpl holoGuiManager;

    public HoloGuiListener(final HoloGuiManagerImpl holoGuiManager)
    {
        this.holoGuiManager = holoGuiManager;
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event)
    {
        // zamknie gui jesli gracz jakies posiada
        this.holoGuiManager.closeGui(event.getPlayer());
    }

    @EventHandler
    public void onPlayerInteractAir(final PlayerInteractEvent event)
    {
        final Action action = event.getAction();
        if (action != LEFT_CLICK_AIR && action != RIGHT_CLICK_AIR)
        {
            return;
        }

        this.handleGuiClick(event.getPlayer(), event);
    }

    private void handleGuiClick(final Player player, final Cancellable cancellable)
    {
        final HoloContextImpl playerContext = this.holoGuiManager.getPlayerContext(player);
        if (playerContext == null)
        {
            return;
        }

        playerContext.handleClick(player.getLocation());
        cancellable.setCancelled(true);
    }

    /*@EventHandler
    public void onMove(final PlayerMoveEvent event)
    {
        final HoloContextImpl playerContext = this.holoGuiManager.getPlayerContext(event.getPlayer());
        if (playerContext != null)
        {
            playerContext.setCenter(event.getTo());
        }
    }*/
}
