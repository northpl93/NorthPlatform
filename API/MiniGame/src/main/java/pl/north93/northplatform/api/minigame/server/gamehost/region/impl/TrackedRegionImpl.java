package pl.north93.northplatform.api.minigame.server.gamehost.region.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.minigame.server.gamehost.region.ITrackedRegion;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.utils.region.IRegion;

class TrackedRegionImpl implements ITrackedRegion
{
    private final RegionManagerImpl manager;
    private final IRegion           region;
    private final List<Consumer<INorthPlayer>> onEnter = new LinkedList<>();
    private final List<Consumer<INorthPlayer>> onExit = new LinkedList<>();

    public TrackedRegionImpl(final RegionManagerImpl manager, final IRegion region)
    {
        this.manager = manager;
        this.region = region;
    }

    @Override
    public IRegion getRegion()
    {
        return this.region;
    }

    @Override
    public void unTrack()
    {
        this.manager.unTrack(this);
    }

    @Override
    public ITrackedRegion whenEnter(final Consumer<INorthPlayer> player)
    {
        this.onEnter.add(player);
        return this;
    }

    @Override
    public ITrackedRegion whenLeave(final Consumer<INorthPlayer> player)
    {
        this.onExit.add(player);
        return this;
    }

    void fireEntered(final INorthPlayer player)
    {
        for (final Consumer<INorthPlayer> event : this.onEnter)
        {
            event.accept(player);
        }
    }

    void fireExited(final INorthPlayer player)
    {
        for (final Consumer<INorthPlayer> event : this.onExit)
        {
            event.accept(player);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("region", this.region).toString();
    }
}
