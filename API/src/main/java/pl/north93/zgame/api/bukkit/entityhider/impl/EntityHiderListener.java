package pl.north93.zgame.api.bukkit.entityhider.impl;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northspigot.event.entity.EntityPreTrackPlayerEvent;
import pl.north93.zgame.api.bukkit.utils.AutoListener;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class EntityHiderListener implements AutoListener
{
    @Inject
    private EntityHiderImpl entityHider;

    @EventHandler
    public void checkEntityShouldBeTracked(final EntityPreTrackPlayerEvent event)
    {
        final Player player = event.getPlayer();
        final Entity entity = event.getEntity();

        if (this.entityHider.isVisible(player, entity))
        {
            // entity powinno byc widoczne wiec pozwalamy na ztrackowanie go
            return;
        }

        event.setCancelled(true);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
