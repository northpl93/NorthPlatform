package pl.north93.zgame.api.bukkit.utils.hologram;

import java.util.Collections;
import java.util.Set;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.entityhider.EntityVisibility;
import pl.north93.zgame.api.bukkit.entityhider.IEntityHider;

public class PlayerVisibility implements IHologramVisibility
{
    private final Player[] players;

    public PlayerVisibility(final Player... players)
    {
        this.players = players;
    }

    @Override
    public void setup(final IEntityHider entityHider, final ArmorStand hologramEntity)
    {
        final Set<Entity> entities = Collections.singleton(hologramEntity);

        entityHider.setVisibility(EntityVisibility.HIDDEN, entities);
        for (final Player player : this.players)
        {
            entityHider.setVisibility(player, EntityVisibility.VISIBLE, entities);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("players", this.players).toString();
    }
}
