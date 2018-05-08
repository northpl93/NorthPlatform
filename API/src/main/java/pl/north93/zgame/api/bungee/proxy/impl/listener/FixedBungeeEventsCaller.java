package pl.north93.zgame.api.bungee.proxy.impl.listener;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import pl.north93.zgame.api.bungee.proxy.event.PlayerLateDisconnectEvent;

/**
 * Wywołuje nasze poprawione eventy bungeecorda.
 */
public class FixedBungeeEventsCaller implements Listener
{
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDisconnect(final PlayerDisconnectEvent event)
    {
        final ProxiedPlayer player = event.getPlayer();
        if (player.getServer() != null)
        {
            // gracz jest połączony z jakimś serwerem, poczekamy na rozłączenie
            // i obsłużymy to w metodzie niżej
            return;
        }

        final PlayerLateDisconnectEvent newEvent = new PlayerLateDisconnectEvent(player);
        ProxyServer.getInstance().getPluginManager().callEvent(newEvent);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerDisconnect(final ServerDisconnectEvent event)
    {
        final ProxiedPlayer player = event.getPlayer();
        if (player.isConnected())
        {
            // gracz nie wyszedł z proxy, więc ignorujemy ten event
            return;
        }

        final PlayerLateDisconnectEvent newEvent = new PlayerLateDisconnectEvent(player);
        ProxyServer.getInstance().getPluginManager().callEvent(newEvent);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerKick(final ServerKickEvent event)
    {
        if (event.getState() != ServerKickEvent.State.CONNECTED || event.getCancelServer() != null)
        {
            return;
        }

        final PlayerLateDisconnectEvent newEvent = new PlayerLateDisconnectEvent(event.getPlayer());
        ProxyServer.getInstance().getPluginManager().callEvent(newEvent);
    }
}
