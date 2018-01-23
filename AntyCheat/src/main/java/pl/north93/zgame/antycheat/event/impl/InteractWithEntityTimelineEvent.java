package pl.north93.zgame.antycheat.event.impl;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import pl.north93.zgame.antycheat.event.AbstractTimelineEvent;

public class InteractWithEntityTimelineEvent extends AbstractTimelineEvent
{
    private final Entity entity; // referencja na atakowane entity
    private final EntityAction action;

    public InteractWithEntityTimelineEvent(final Player player, final Entity entity, final EntityAction action)
    {
        super(player);
        this.entity = entity;
        this.action = action;
    }

    public Entity getEntity()
    {
        return this.entity;
    }

    public EntityAction getAction()
    {
        return this.action;
    }

    public enum EntityAction
    {
        INTERACT,
        ATTACK,
        INTERACT_AT
    }
}
