package pl.arieals.api.minigame.shared.impl;

import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.shared.api.arena.RemoteArena;
import pl.north93.zgame.api.global.component.annotations.PostInject;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.redis.observable.Hash;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;

public class ArenaManager
{
    @Inject
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

    public void removeArena(final UUID arenaId)
    {
        this.arenas.delete(arenaId.toString());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("arenas", this.arenas).toString();
    }
}
