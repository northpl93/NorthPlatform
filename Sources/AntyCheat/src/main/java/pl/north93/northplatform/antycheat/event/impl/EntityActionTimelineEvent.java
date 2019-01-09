package pl.north93.northplatform.antycheat.event.impl;

import net.minecraft.server.v1_12_R1.PacketPlayInEntityAction.EnumPlayerAction;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.ToString;
import pl.north93.northplatform.antycheat.event.AbstractTimelineEvent;

@Getter
@ToString
public class EntityActionTimelineEvent extends AbstractTimelineEvent
{
    private final EnumPlayerAction action;

    public EntityActionTimelineEvent(final Player player, final EnumPlayerAction action)
    {
        super(player);
        this.action = action;
    }
}
