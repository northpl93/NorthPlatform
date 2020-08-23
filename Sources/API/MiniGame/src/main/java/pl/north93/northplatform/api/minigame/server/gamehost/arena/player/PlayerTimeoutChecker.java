package pl.north93.northplatform.api.minigame.server.gamehost.arena.player;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.server.IBukkitExecutor;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArenaManager;

/**
 * Task sprawdzający czy wysłane zapytania dołączenia gracza do serwera
 * się nie przedawniły.
 *
 * @see PlayersManager#checkTimeouts()
 */
public class PlayerTimeoutChecker implements Runnable
{
    private final LocalArenaManager localArenaManager;

    @Bean
    private PlayerTimeoutChecker(final LocalArenaManager localArenaManager, final IBukkitExecutor executor)
    {
        this.localArenaManager = localArenaManager;
        executor.syncTimer(100, this);
    }

    @Override
    public void run()
    {
        final List<LocalArena> arenas = this.localArenaManager.getArenas();

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
