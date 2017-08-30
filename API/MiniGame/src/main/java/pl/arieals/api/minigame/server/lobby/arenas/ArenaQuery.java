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
        // na ekstremalne wypadki zakladamy ze moze przyjsc null. I tak java to pewnie zoptymalizuje
        if (arena == null)
        {
            return false;
        }

        // jesli this.gamePhase nie jest nullem to sprawdzamy
        if (this.gamePhase != null && arena.getGamePhase() != this.gamePhase)
        {
            return false;
        }

        // jesli this.worldId nie jest nullem to sprawdzamy
        if (this.worldId != null && !arena.getWorldId().equals(this.worldId))
        {
            return false;
        }

        // todo zaimplemrntowac id i variant minigry

        return true;
    }
}
