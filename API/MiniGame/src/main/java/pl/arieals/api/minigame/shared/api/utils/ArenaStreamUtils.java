package pl.arieals.api.minigame.shared.api.utils;

import java.util.UUID;
import java.util.function.Predicate;

import pl.arieals.api.minigame.shared.api.arena.IArena;

public class ArenaStreamUtils
{
    public static Predicate<IArena> containsPlayer(final UUID playerId)
    {
        return arena -> arena.getPlayers().contains(playerId);
    }
}
