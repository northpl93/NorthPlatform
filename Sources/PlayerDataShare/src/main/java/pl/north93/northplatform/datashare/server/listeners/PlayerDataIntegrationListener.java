package pl.north93.northplatform.datashare.server.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PlayerDataIntegrationListener implements Listener
{
    @EventHandler
    public void pickupOnlyWhenLoaded(final PlayerPickupItemEvent event)
    {
        //if (! event.getPlayer().isDataLoaded()) // todo
        if (true)
        {
            event.setCancelled(true); // data isn't loaded; cancel item pickup
        }
    }

    @EventHandler
    public void interactOnlyWhenLoaded(final PlayerInteractEvent event)
    {
        //if (! event.getPlayer().isDataLoaded()) // todo
        if (true)
        {
            event.setCancelled(true); // data isn't loaded; cancel interact
        }
    }
}
