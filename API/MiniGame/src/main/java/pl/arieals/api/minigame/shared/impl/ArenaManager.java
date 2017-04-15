package pl.arieals.api.minigame.shared.impl;

import java.util.Set;
import java.util.UUID;

import pl.arieals.api.minigame.shared.api.arena.RemoteArena;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.PostInject;
import pl.north93.zgame.api.global.redis.observable.Hash;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;

public class ArenaManager
{
    @InjectComponent("API.Database.Redis.Observer")
    private IObservationManager observer;
    private Hash<RemoteArena>   arenas;

    @PostInject
    private void init()
    {
        this.arenas = this.observer.getHash(RemoteArena.class, "arenas");
    }

    public RemoteArena getArena(final UUID arenaId)
    {
        return this.arenas.get(arenaId.toString());
    }

    public Set<RemoteArena> getAllArenas()
    {
        return this.arenas.values();
    }

    public void setArena(final RemoteArena arena)
    {
        this.arenas.put(arena.getId().toString(), arena);
    }
}
