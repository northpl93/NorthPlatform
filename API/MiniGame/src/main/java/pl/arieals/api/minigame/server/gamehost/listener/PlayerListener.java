package pl.arieals.api.minigame.server.gamehost.listener;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;

public class PlayerListener implements Listener
{
    @InjectComponent("MiniGameApi.Server")
    private MiniGameServer server;

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();

        final GameHostManager gameHostManager = this.server.getServerManager();
        final Optional<LocalArena> arena = gameHostManager.getArenaManager().getArenaAssociatedWith(player.getUniqueId());

        if (arena.isPresent())
        {
            final LocalArena localArena = arena.get();
            localArena.getPlayersManager().playerConnected(player);
        }
        else
        {
            player.sendMessage("Dolaczyles do GameHosta, ale nie znaleziono powiazanej areny"); // debug msg
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event)
    {
        final Player player = event.getPlayer();

        final GameHostManager gameHostManager = this.server.getServerManager();
        final Optional<LocalArena> arena = gameHostManager.getArenaManager().getArenaAssociatedWith(player.getUniqueId());

        if (!arena.isPresent())
        {
            return;
        }

        final LocalArena localArena = arena.get();
        localArena.getPlayersManager().playerDisconnected(player);
    }
}
