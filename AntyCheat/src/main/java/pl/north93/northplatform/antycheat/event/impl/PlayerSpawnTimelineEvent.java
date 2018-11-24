package pl.north93.northplatform.antycheat.event.impl;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.ToString;
import pl.north93.northplatform.antycheat.event.AbstractTimelineEvent;

@Getter
@ToString
public class PlayerSpawnTimelineEvent extends AbstractTimelineEvent
{
    private final SpawnReason spawnReason;

    public PlayerSpawnTimelineEvent(final Player player, final SpawnReason spawnReason)
    {
        super(player);
        this.spawnReason = spawnReason;
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
}
