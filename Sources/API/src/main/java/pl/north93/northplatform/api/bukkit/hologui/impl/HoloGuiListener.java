package pl.north93.northplatform.api.bukkit.hologui.impl;

import static org.bukkit.event.block.Action.LEFT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;


import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import pl.north93.northplatform.api.bukkit.player.IBukkitPlayers;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.server.AutoListener;

public class HoloGuiListener implements AutoListener
{
    private final HoloGuiManagerImpl holoGuiManager;
    private final IBukkitPlayers bukkitPlayers;

    public HoloGuiListener(final HoloGuiManagerImpl holoGuiManager, final IBukkitPlayers bukkitPlayers)
    {
        this.holoGuiManager = holoGuiManager;
        this.bukkitPlayers = bukkitPlayers;
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event)
    {
        final INorthPlayer player = this.bukkitPlayers.getPlayer(event.getPlayer());

        // zamknie gui jesli gracz jakies posiada
        this.holoGuiManager.closeGui(player);
    }

    @EventHandler
    public void onPlayerInteractAir(final PlayerInteractEvent event)
    {
        final Action action = event.getAction();
        if (action != LEFT_CLICK_AIR && action != RIGHT_CLICK_AIR)
        {
            return;
        }

        final INorthPlayer player = this.bukkitPlayers.getPlayer(event.getPlayer());
        this.handleGuiClick(player, event);
    }

    private void handleGuiClick(final INorthPlayer player, final Cancellable cancellable)
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
