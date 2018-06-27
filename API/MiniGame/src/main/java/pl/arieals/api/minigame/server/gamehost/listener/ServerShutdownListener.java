package pl.arieals.api.minigame.server.gamehost.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArenaManager;
import pl.north93.zgame.api.bukkit.server.event.ShutdownCancelledEvent;
import pl.north93.zgame.api.bukkit.server.event.ShutdownScheduledEvent;
import pl.north93.zgame.api.bukkit.utils.AutoListener;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class ServerShutdownListener implements AutoListener
{
    @Inject
    private MiniGameServer miniGameServer;

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
        final GameHostManager serverManager = this.miniGameServer.getServerManager();
        final LocalArenaManager arenaManager = serverManager.getArenaManager();

        final int arenas = arenaManager.getArenas().size();
        final int target = serverManager.getMiniGameConfig().getArenas();

        for (int i = 0; i < target - arenas; i++)
        {
            // tworzymy nowe areny jesli ich ilosc zmniejszyla sie gdy
            // bylo zaplanowane wylaczenie
            arenaManager.createArena();
        }
    }
}
