package pl.north93.northplatform.api.minigame.server.lobby.arenas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

import pl.north93.northplatform.api.minigame.shared.api.GameIdentity;
import pl.north93.northplatform.api.minigame.shared.api.GamePhase;
import pl.north93.northplatform.api.minigame.shared.api.arena.IArena;

public class ArenaQuery implements Predicate<IArena>
{
    private GameIdentity          gameIdentity;
    private Collection<GamePhase> gamePhase = new ArrayList<>(0);
    private String                worldId;

    public static ArenaQuery create()
    {
        return new ArenaQuery();
    }

    public ArenaQuery miniGame(final GameIdentity gameIdentity)
    {
        this.gameIdentity = gameIdentity;
        return this;
    }

    public ArenaQuery gamePhase(final GamePhase gamePhase)
    {
        this.gamePhase.add(gamePhase);
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

        if (this.gameIdentity != null && !arena.getMiniGame().equals(this.gameIdentity))
        {
            return false;
        }

        // jesli this.gamePhase nie jest puste i nie zawiera gamephase areny to return false
        if (! this.gamePhase.isEmpty() && ! this.gamePhase.contains(arena.getGamePhase()))
        {
            return false;
        }

        // jesli this.worldId nie jest nullem to sprawdzamy
        if (this.worldId != null && !arena.getWorldId().equals(this.worldId))
        {
            return false;
        }

        return true;
    }
}
