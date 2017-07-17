package pl.arieals.api.minigame.server.shared.api;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PlayerJoinInfo
{
    private UUID    uuid;
    private Boolean isVip;
    private Boolean spectator;

    public PlayerJoinInfo() // serialization
    {
    }

    public PlayerJoinInfo(final UUID uuid, final Boolean isVip, final Boolean spectator)
    {
        this.uuid = uuid;
        this.isVip = isVip;
        this.spectator = spectator;
    }

    public UUID getUuid()
    {
        return this.uuid;
    }

    public Boolean isVip()
    {
        return this.isVip;
    }

    public Boolean isSpectator()
    {
        return this.spectator;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("uuid", this.uuid).append("isVip", this.isVip).append("spectator", this.spectator).toString();
    }
}
