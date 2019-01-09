package pl.north93.northplatform.api.minigame.shared.api.arena.netevent;

import java.util.UUID;

import pl.north93.northplatform.api.minigame.shared.api.GameIdentity;
import pl.north93.northplatform.api.global.redis.event.INetEvent;

public interface IArenaNetEvent extends INetEvent
{
    UUID getArenaId();

    GameIdentity getMiniGame();
}
