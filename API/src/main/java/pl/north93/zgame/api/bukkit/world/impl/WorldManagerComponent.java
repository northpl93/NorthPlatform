package pl.north93.zgame.api.bukkit.world.impl;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;

import com.google.common.base.Preconditions;

import net.minecraft.server.v1_12_R1.WorldServer;

import pl.north93.zgame.api.bukkit.utils.xml.XmlChunk;
import pl.north93.zgame.api.bukkit.world.IWorldLoadCallback;
import pl.north93.zgame.api.bukkit.world.IWorldManager;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class WorldManagerComponent extends Component implements IWorldManager
{
    @Inject
    private ChunkLoadManager chunkLoadManager;
    
    @Override
    protected void enableComponent()
    {
        // TODO: handle main world
    }

    @Override
    protected void disableComponent()
    {
    }

    @Override
    public IWorldLoadCallback loadWorld(String name, WorldCreator creator, boolean keepEntireWorldLoaded, boolean disableGenerateNewChunks)
    {
        WorldServer worldServer = NmsWorldUtils.createWorldInstance(name, null);
        
        if ( worldServer == null )
        {
            return null;
        }
        
        Bukkit.getPluginManager().callEvent(new WorldInitEvent(worldServer.getWorld()));
        NorthChunkProvider.inject(worldServer, keepEntireWorldLoaded, disableGenerateNewChunks);
        
        getLogger().info("Initialized world " + name + " (keepWorldLoaded: " + keepEntireWorldLoaded + ", disableGenerateChunks: " + disableGenerateNewChunks);
        
        Bukkit.getPluginManager().callEvent(new WorldLoadEvent(worldServer.getWorld()));
        
        WorldLoadCallback callback = new WorldLoadCallback(worldServer.getWorld());
        
        if ( keepEntireWorldLoaded )
        {
            chunkLoadManager.start(ChunkLoadTask.loadAllChunks(callback));
        }
        else if ( worldServer.getWorld().getKeepSpawnInMemory() )
        {
            chunkLoadManager.start(ChunkLoadTask.loadSpawn(callback));
        }
        else
        {
            callback.callComplete();
        }
        
        return callback;
    }

    @Override
    public boolean tryUnloadWorld(World world)
    {
        return unloadWorld0(world, false);
    }

    @Override
    public void unloadWorld(World world)
    {
        Preconditions.checkState(unloadWorld0(world, true));
    }

    private boolean unloadWorld0(World world, boolean force)
    {
        boolean result = NmsWorldUtils.unloadWorld(world, force);
        
        if ( result )
        {
            getLogger().info("Unloaded world " + world.getName());
        }
        
        return result;
    }
    
    @Override
    public void trimWorld(World world, String target, Collection<? extends XmlChunk> chunks)
    {
        // TODO Auto-generated method stub
        
    }
}
