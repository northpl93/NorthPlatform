package pl.arieals.api.minigame.server.shared.api;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PlayerJoinInfo
{
    private UUID    uuid;
    private Boolean isVip;

    public PlayerJoinInfo() // serialization
    {
    }

    public PlayerJoinInfo(final UUID uuid, final Boolean isVip)
    {
        this.uuid = uuid;
        this.isVip = isVip;
    }

    public UUID getUuid()
    {
        return this.uuid;
    }

    public Boolean isVip()
    {
        return this.isVip;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("uuid", this.uuid).append("isVip", this.isVip).toString();
    }
}
