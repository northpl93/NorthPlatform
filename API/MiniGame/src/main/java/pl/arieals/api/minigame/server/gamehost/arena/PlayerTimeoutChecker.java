package pl.arieals.api.minigame.server.gamehost.arena;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PlayerTimeoutChecker implements Runnable
{
    private final LocalArenaManager arenaManager;

    public PlayerTimeoutChecker(final LocalArenaManager arenaManager)
    {
        this.arenaManager = arenaManager;
    }

    @Override
    public void run()
    {
        final List<LocalArena> arenas = this.arenaManager.getArenas();

        for (final LocalArena arena : arenas)
        {
            arena.getPlayersManager().checkTimeouts();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("arenaManager", this.arenaManager).toString();
    }
}
