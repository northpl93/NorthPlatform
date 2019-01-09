package pl.north93.northplatform.api.minigame.server.gamehost.arena.player;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.minigame.server.MiniGameServer;
import pl.north93.northplatform.api.minigame.server.gamehost.GameHostManager;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.bukkit.server.IBukkitExecutor;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;

/**
 * Task sprawdzający czy wysłane zapytania dołączenia gracza do serwera
 * się nie przedawniły.
 *
 * @see PlayersManager#checkTimeouts()
 */
public class PlayerTimeoutChecker implements Runnable
{
    private final MiniGameServer server;

    @Bean
    private PlayerTimeoutChecker(final MiniGameServer server, final IBukkitExecutor executor)
    {
        this.server = server;
        executor.syncTimer(100, this);
    }

    @Override
    public void run()
    {
        final GameHostManager serverManager = this.server.getServerManager();
        final List<LocalArena> arenas = serverManager.getArenaManager().getArenas();

        for (final LocalArena arena : arenas)
        {
            arena.getPlayersManager().checkTimeouts();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
