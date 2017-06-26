package pl.arieals.api.minigame.server.gamehost.listener;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.arena.PlayersManager;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerJoinWithoutArenaEvent;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class PlayerListener implements Listener
{
    @Inject
    private MiniGameServer server;

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();

        final GameHostManager gameHostManager = this.server.getServerManager();
        final Optional<LocalArena> arena = gameHostManager.getArenaManager().getArenaAssociatedWith(player.getUniqueId());

        if (arena.isPresent())
        {
            final PlayersManager playersManager = arena.get().getPlayersManager();
            playersManager.playerConnected(player);
        }
        else
        {
            Bukkit.getPluginManager().callEvent(new PlayerJoinWithoutArenaEvent(player));
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event)
    {
        final Player player = event.getPlayer();

        final GameHostManager gameHostManager = this.server.getServerManager();
        final Optional<LocalArena> arena = gameHostManager.getArenaManager().getArenaAssociatedWith(player.getUniqueId());

        if (! arena.isPresent())
        {
            return;
        }

        final PlayersManager playersManager = arena.get().getPlayersManager();
        playersManager.playerDisconnected(player);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
