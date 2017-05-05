package pl.arieals.api.minigame.server.gamehost.region;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;

import pl.north93.zgame.api.bukkit.utils.region.IRegion;

public interface IRegionManager
{
    ITrackedRegion create(IRegion region);

    Set<ITrackedRegion> getRegions(World world);

    Set<ITrackedRegion> getRegions(Location location);
}
