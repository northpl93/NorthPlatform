package pl.arieals.api.minigame.shared.api.status;

import javax.annotation.Nullable;

import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.shared.api.GameIdentity;

public final class InGameStatus implements IPlayerStatus
{
    private UUID         serverId;
    private UUID         arenaId;
    private GameIdentity game;

    public InGameStatus()
    {
    }

    public InGameStatus(final UUID serverId, final @Nullable UUID arenaId, final GameIdentity game)
    {
        this.serverId = serverId;
        this.arenaId = arenaId;
        this.game = game;
    }

    @Override
    public UUID getServerId()
    {
        return this.serverId;
    }

    @Override
    public StatusType getType()
    {
        return StatusType.GAME;
    }

    @Nullable
    public UUID getArenaId()
    {
        return this.arenaId;
    }

    public GameIdentity getGame()
    {
        return this.game;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || this.getClass() != o.getClass())
        {
            return false;
        }
        final InGameStatus that = (InGameStatus) o;
        return Objects.equals(this.serverId, that.serverId) && Objects.equals(this.arenaId, that.arenaId) && Objects.equals(this.game, that.game);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.serverId, this.arenaId, this.game);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("serverId", this.serverId).append("arenaId", this.arenaId).append("game", this.game).toString();
    }
}
