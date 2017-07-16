package pl.arieals.api.minigame.server.gamehost.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import pl.north93.zgame.api.bukkit.server.event.ShutdownCancelledEvent;
import pl.north93.zgame.api.bukkit.server.event.ShutdownScheduledEvent;

public class ServerShutdownListener implements Listener
{
    @EventHandler
    public void onShutdownRequest(final ShutdownScheduledEvent event)
    {
        if (Bukkit.getOnlinePlayers().isEmpty())
        {
            return;
        }

        // przekladamy wylaczanie jesli mamy graczy na serwerze.
        // Btw. moze wymyslimy cos madrzejszego?
        event.setCancelled(true);
    }

    @EventHandler
    public void onShutdownCancelled(final ShutdownCancelledEvent event)
    {
        // todo sprawdzic czy ilosc aren sie zgadza i ew. utworzyc nowe
    }
}
