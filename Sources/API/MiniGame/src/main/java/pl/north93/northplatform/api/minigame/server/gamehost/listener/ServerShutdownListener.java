package pl.north93.northplatform.api.minigame.server.gamehost.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import pl.north93.northplatform.api.bukkit.server.event.ShutdownCancelledEvent;
import pl.north93.northplatform.api.bukkit.server.event.ShutdownScheduledEvent;
import pl.north93.northplatform.api.bukkit.server.AutoListener;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.minigame.server.gamehost.GameHostManager;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArenaManager;

public class ServerShutdownListener implements AutoListener
{
    @Inject
    private GameHostManager gameHostManager;
    @Inject
    private LocalArenaManager localArenaManager;

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
        final int arenas = this.localArenaManager.getArenas().size();
        final int target = this.gameHostManager.getMiniGameConfig().getArenas();

        for (int i = 0; i < target - arenas; i++)
        {
            // tworzymy nowe areny jesli ich ilosc zmniejszyla sie gdy
            // bylo zaplanowane wylaczenie
            this.localArenaManager.createArena();
        }
    }
}
