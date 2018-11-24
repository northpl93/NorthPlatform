package pl.north93.northplatform.api.bukkit.world.impl;

import org.bukkit.World;

import pl.north93.northplatform.api.bukkit.utils.SimpleSyncCallback;
import pl.north93.northplatform.api.bukkit.world.IWorldLoadCallback;

/*default*/ class WorldLoadCallback extends SimpleSyncCallback implements IWorldLoadCallback
{
    private final World world;
    
    WorldLoadCallback(World world)
    {
        this.world = world;
    }
    
    @Override
    public World getWorld()
    {
        return world;
    }
}
