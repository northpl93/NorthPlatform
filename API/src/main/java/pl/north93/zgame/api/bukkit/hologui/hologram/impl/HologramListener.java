package pl.north93.zgame.api.bukkit.hologui.hologram.impl;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.event.EventHandler;

import pl.north93.northspigot.event.entity.EntityTrackedPlayerEvent;
import pl.north93.zgame.api.bukkit.utils.AutoListener;

public class HologramListener implements AutoListener
{
    @EventHandler
    public void handleHologramArmorStandTrackedEvent(final EntityTrackedPlayerEvent event)
    {
        final CraftEntity entity = (CraftEntity) event.getEntity();
        if (entity.getHandle() instanceof HologramArmorStand)
        {
            final HologramArmorStand hologramArmorStand = (HologramArmorStand) entity.getHandle();

            hologramArmorStand.getHoloLine().playerStartedTracking(event.getPlayer());
        }
    }
}
