package pl.arieals.api.minigame.shared.api.arena.netevent;

import java.util.UUID;

import pl.north93.zgame.api.global.redis.event.INetEvent;

public interface IArenaNetEvent extends INetEvent
{
    UUID getArenaId();

    String getMiniGameId();
}
