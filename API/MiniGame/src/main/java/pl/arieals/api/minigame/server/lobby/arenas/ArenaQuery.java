package pl.arieals.api.minigame.server.lobby.arenas;

import java.util.function.Predicate;

import pl.arieals.api.minigame.shared.api.arena.IArena;

public class ArenaQuery implements Predicate<IArena>
{
    private String miniGameId;
    private String worldId;

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

        return false;
    }
}
