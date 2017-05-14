package pl.arieals.api.minigame.server.gamehost.event.arena.gamephase;

import org.bukkit.Bukkit;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.ArenaEvent;

public final class GamePhaseEventFactory
{
    private final static GamePhaseEventFactory INSTANCE = new GamePhaseEventFactory();

    private GamePhaseEventFactory()
    {
    }

    public static GamePhaseEventFactory getInstance()
    {
        return INSTANCE;
    }

    public ArenaEvent createEvent(final LocalArena arena)
    {
        switch (arena.getGamePhase())
        {
            case RESTARTING:
                return new GameRestartEvent(arena);
            case LOBBY:
                return new GameInitEvent(arena);
            case STARTED:
                return new GameStartedEvent(arena);
            case POST_GAME:
                return new GameEndEvent(arena);
            default:
                throw new IllegalArgumentException("Arena " + arena + " is in invalid gamephase");
        }
    }

    public void callEvent(final LocalArena arena)
    {
        Bukkit.getPluginManager().callEvent(this.createEvent(arena));
    }
}
