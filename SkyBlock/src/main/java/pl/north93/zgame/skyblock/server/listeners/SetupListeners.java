package pl.north93.zgame.skyblock.server.listeners;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.API;
import pl.north93.zgame.skyblock.api.ServerMode;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyblock.server.listeners.islandhost.DieListener;
import pl.north93.zgame.skyblock.server.listeners.islandhost.TeleportListener;
import pl.north93.zgame.skyblock.server.listeners.islandhost.WorldModificationListener;
import pl.north93.zgame.skyblock.server.listeners.lobby.SkyLobbyJoin;

public class SetupListeners
{
    public static void setup(final SkyBlockServer server)
    {
        final Plugin plugin = ((BukkitApiCore) API.getApiCore()).getPluginMain();
        final PluginManager pluginManager = Bukkit.getPluginManager();
        if (server.getServerMode().equals(ServerMode.ISLAND_HOST))
        {
            pluginManager.registerEvents(new WorldModificationListener(), plugin);
            pluginManager.registerEvents(new DieListener(), plugin);
            pluginManager.registerEvents(new TeleportListener(), plugin);
        }
        else
        {
            pluginManager.registerEvents(new SkyLobbyJoin(), plugin);
        }
    }
}
