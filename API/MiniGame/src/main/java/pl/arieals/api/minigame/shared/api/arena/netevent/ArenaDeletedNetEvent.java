package pl.arieals.api.minigame.shared.api.arena.netevent;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.shared.api.GameIdentity;

public class ArenaDeletedNetEvent implements IArenaNetEvent
{
    private UUID         arenaId;
    private UUID         serverId;
    private GameIdentity miniGame;

    public ArenaDeletedNetEvent()
    {
    }

    public ArenaDeletedNetEvent(final UUID arenaId, final UUID serverId, final GameIdentity miniGame)
    {
        this.arenaId = arenaId;
        this.serverId = serverId;
        this.miniGame = miniGame;
    }

    @Override
    public UUID getArenaId()
    {
        return this.arenaId;
    }

    public UUID getServerId()
    {
        return this.serverId;
    }

    @Override
    public GameIdentity getMiniGame()
    {
        return this.miniGame;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("arenaId", this.arenaId).append("serverId", this.serverId).append("miniGame", this.miniGame).toString();
    }
}
