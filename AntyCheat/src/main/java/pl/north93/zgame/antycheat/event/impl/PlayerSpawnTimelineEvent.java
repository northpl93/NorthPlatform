package pl.north93.zgame.antycheat.event.impl;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.antycheat.event.AbstractTimelineEvent;

public class PlayerSpawnTimelineEvent extends AbstractTimelineEvent
{
    private final SpawnReason spawnReason;

    public PlayerSpawnTimelineEvent(final Player player, final SpawnReason spawnReason)
    {
        super(player);
        this.spawnReason = spawnReason;
    }

    public SpawnReason getSpawnReason()
    {
        return this.spawnReason;
    }

    public boolean isInitial()
    {
        return this.spawnReason == SpawnReason.SERVER_JOIN;
    }

    public enum SpawnReason
    {
        SERVER_JOIN,
        RESPAWN_SCREEN,
        BED
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("spawnReason", this.spawnReason).toString();
    }
}
