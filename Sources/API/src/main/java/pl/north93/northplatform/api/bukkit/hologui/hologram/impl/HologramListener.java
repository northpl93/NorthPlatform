package pl.north93.northplatform.api.bukkit.hologui.hologram.impl;

import java.util.Collection;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.player.event.PlayerPlatformLocaleChangedEvent;
import pl.north93.northplatform.api.bukkit.utils.AutoListener;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northspigot.event.entity.EntityTrackedPlayerEvent;

public class HologramListener implements AutoListener
{
    @Inject
    private HologramManager hologramManager;

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

    @EventHandler
    public void updateHologramOnLanguageChange(final PlayerPlatformLocaleChangedEvent event)
    {
        this.hologramManager.updatePlayerHolograms(event.getPlayer());
    }

    @EventHandler
    public void initHologramsOnChunkLoad(final ChunkLoadEvent event)
    {
        final Collection<HologramImpl> holograms = this.hologramManager.getHologramsInChunk(event.getChunk());
        holograms.forEach(HologramImpl::tryInitHologram);
    }

    @EventHandler
    public void deleteHologramsFromUnloadedWorld(final WorldUnloadEvent event)
    {
        this.hologramManager.removeFromWorld(event.getWorld());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
