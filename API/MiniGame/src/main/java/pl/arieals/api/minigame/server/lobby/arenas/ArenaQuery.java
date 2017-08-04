package pl.arieals.api.minigame.server.lobby.arenas;

import java.util.function.Predicate;

import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.api.minigame.shared.api.arena.IArena;

public class ArenaQuery implements Predicate<IArena>
{
    private GamePhase gamePhase = GamePhase.LOBBY; // default lobby
    private String    miniGameId;
    private String    worldId;

    public static ArenaQuery create()
    {
        return new ArenaQuery();
    }

    public ArenaQuery gamePhase(final GamePhase gamePhase)
    {
        this.gamePhase = gamePhase;
        return this;
    }

    public ArenaQuery miniGame(final String miniGameId)
    {
        this.miniGameId = miniGameId;
        return this;
    }

    public ArenaQuery world(final String worldId)
    {
        this.worldId = worldId;
        return this;
    }

    @Override
    public boolean test(final IArena arena)
    {
        if (arena.getGamePhase() == this.gamePhase)
        {
            return true;
        }
        // todo
        return true;
    }
}
