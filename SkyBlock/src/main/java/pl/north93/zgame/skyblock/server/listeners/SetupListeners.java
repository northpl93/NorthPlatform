package pl.north93.zgame.skyblock.server.listeners;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.API;
import pl.north93.zgame.skyblock.api.ServerMode;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyblock.server.listeners.islandhost.DieListener;
import pl.north93.zgame.skyblock.server.listeners.islandhost.WorldModificationListener;

public class SetupListeners
{
    public static void setup(final SkyBlockServer server)
    {
        final Plugin plugin = ((BukkitApiCore) API.getApiCore()).getPluginMain();
        if (server.getServerMode().equals(ServerMode.ISLAND_HOST))
        {
            //Bukkit.getPluginManager().registerEvents(new ServerJoinListener(), plugin);
            Bukkit.getPluginManager().registerEvents(new WorldModificationListener(), plugin);
            Bukkit.getPluginManager().registerEvents(new DieListener(), plugin);
        }
        else
        {
            //
        }
    }
}
