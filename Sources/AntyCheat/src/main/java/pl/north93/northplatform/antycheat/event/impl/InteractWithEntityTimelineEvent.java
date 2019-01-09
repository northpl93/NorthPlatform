package pl.north93.northplatform.antycheat.event.impl;

import javax.annotation.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.ToString;
import pl.north93.northplatform.antycheat.event.AbstractTimelineEvent;

@Getter
@ToString
public class InteractWithEntityTimelineEvent extends AbstractTimelineEvent
{
    @Nullable
    private final Entity entity; // referencja na atakowane entity
    private final EntityAction action;

    public InteractWithEntityTimelineEvent(final Player player, @Nullable final Entity entity, final EntityAction action)
    {
        super(player);
        this.entity = entity;
        this.action = action;
    }

    public enum EntityAction
    {
        INTERACT,
        ATTACK,
        INTERACT_AT
    }
}
