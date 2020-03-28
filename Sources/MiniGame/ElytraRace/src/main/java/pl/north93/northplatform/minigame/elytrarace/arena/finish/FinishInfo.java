package pl.north93.northplatform.minigame.elytrarace.arena.finish;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public abstract class FinishInfo
{
    private final UUID   uuid;
    private final String displayName;

    public FinishInfo(final UUID uuid, final String displayName)
    {
        this.uuid = uuid;
        this.displayName = displayName;
    }

    public UUID getUuid()
    {
        return this.uuid;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("uuid", this.uuid).append("displayName", this.displayName).toString();
    }
}
