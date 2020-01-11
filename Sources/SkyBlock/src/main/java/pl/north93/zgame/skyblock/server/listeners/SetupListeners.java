package pl.north93.zgame.skyblock.server.listeners;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import pl.north93.northspigot.event.blockchange.BlockChangeInvoker;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyblock.server.listeners.islandhost.BadMobsListener;
import pl.north93.zgame.skyblock.server.listeners.islandhost.DieListener;
import pl.north93.zgame.skyblock.server.listeners.islandhost.RankingHandler;
import pl.north93.zgame.skyblock.server.listeners.islandhost.TeleportListener;
import pl.north93.zgame.skyblock.server.listeners.islandhost.WorldModificationListener;
import pl.north93.zgame.skyblock.server.listeners.lobby.Launchpad;
import pl.north93.zgame.skyblock.server.listeners.lobby.SkyLobbyJoin;
import pl.north93.zgame.skyblock.shared.api.ServerMode;

public class SetupListeners
{
    @Inject
    private static BukkitApiCore apiCore;

    public static void setup(final SkyBlockServer server)
    {
        final Plugin plugin = apiCore.getPluginMain();
        final PluginManager pluginManager = Bukkit.getPluginManager();
        if (server.getServerMode().equals(ServerMode.ISLAND_HOST))
        {
            pluginManager.registerEvents(new WorldModificationListener(), plugin);
            pluginManager.registerEvents(new DieListener(), plugin);
            pluginManager.registerEvents(new TeleportListener(), plugin);
            pluginManager.registerEvents(new BadMobsListener(), plugin);
            BlockChangeInvoker.setHandler(new RankingHandler());
            //pluginManager.registerEvents(new ItemRecoveryListener(), plugin); // exp recovery - disable
        }
        else
        {
            pluginManager.registerEvents(new SkyLobbyJoin(), plugin);
            pluginManager.registerEvents(new Launchpad(), plugin);
        }
    }
}