package pl.north93.zgame.api.bukkit.world.impl;

import org.bukkit.World;

import pl.north93.zgame.api.bukkit.utils.SimpleSyncCallback;
import pl.north93.zgame.api.bukkit.world.IWorldLoadCallback;

public class WorldLoadCallback extends SimpleSyncCallback implements IWorldLoadCallback
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
