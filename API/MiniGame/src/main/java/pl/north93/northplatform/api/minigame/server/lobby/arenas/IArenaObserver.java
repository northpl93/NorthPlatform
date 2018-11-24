package pl.north93.northplatform.api.minigame.server.lobby.arenas;

import pl.north93.northplatform.api.minigame.shared.api.arena.IArena;

/**
 * Interfejs obserwatora zdarzen w sieci dotyczacych aren minigier.
 *
 * @see IArenaClient#observe(ArenaQuery, IArenaObserver)
 */
public interface IArenaObserver
{
    default void arenaCreated(final IArena arena)
    {
    }

    default void arenaUpdated(final IArena arena)
    {
    }

    default void arenaRemoved(final IArena arena)
    {
    }
}
