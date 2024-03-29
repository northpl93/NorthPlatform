package pl.north93.northplatform.api.minigame.server.gamehost.region.impl;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.World;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.server.IBukkitServerManager;
import pl.north93.northplatform.api.bukkit.utils.region.IRegion;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.minigame.server.gamehost.region.IRegionManager;
import pl.north93.northplatform.api.minigame.server.gamehost.region.ITrackedRegion;

public class RegionManagerImpl implements IRegionManager
{
    @Inject
    private IBukkitServerManager serverManager;
    private final Collection<TrackedRegionImpl> regions = new ConcurrentLinkedQueue<>(); // potrzebujemy concurrent, indexy są nieważne.

    public RegionManagerImpl()
    {
        this.serverManager.registerEvents(new MoveListener(this));
    }

    @Override
    public ITrackedRegion create(final IRegion region)
    {
        final TrackedRegionImpl impl = new TrackedRegionImpl(this, region);
        this.regions.add(impl);
        return impl;
    }

    @Override
    public Set<ITrackedRegion> getRegions(final World world)
    {
        return this.regions.stream().filter(region -> region.getRegion().getWorld().equals(world)).collect(Collectors.toSet());
    }

    @Override
    public Set<ITrackedRegion> getRegions(final Location location)
    {
        return this.regions.stream().filter(region -> region.getRegion().contains(location)).collect(Collectors.toSet());
    }

    /*default*/ void unTrack(final TrackedRegionImpl impl)
    {
        this.regions.remove(impl);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("regions", this.regions).toString();
    }
}
