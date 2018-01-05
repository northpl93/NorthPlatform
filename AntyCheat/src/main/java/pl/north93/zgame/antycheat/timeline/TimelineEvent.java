package pl.north93.zgame.antycheat.timeline;

import java.time.Instant;

import org.bukkit.entity.Player;

public interface TimelineEvent
{
    Player getOwner();

    Instant getCreationTime();
}
