package pl.arieals.api.minigame.server.lobby.arenas;

import pl.arieals.api.minigame.shared.api.arena.IArena;

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
