package pl.arieals.api.minigame.shared.api.arena.netevent;

import java.util.UUID;

public interface IArenaNetEvent
{
    UUID getArenaId();

    ArenaEventType getEventType();
}