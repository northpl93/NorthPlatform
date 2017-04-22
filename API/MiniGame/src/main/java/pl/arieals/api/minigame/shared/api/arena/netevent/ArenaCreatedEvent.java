package pl.arieals.api.minigame.shared.api.arena.netevent;

import java.util.UUID;

public class ArenaCreatedEvent implements IArenaNetEvent
{
    private UUID uuid;

    @Override
    public UUID getArenaId()
    {
        return null;
    }

    @Override
    public ArenaEventType getEventType()
    {
        return null;
    }
}
