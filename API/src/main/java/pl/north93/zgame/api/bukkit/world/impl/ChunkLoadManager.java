package pl.north93.zgame.api.bukkit.world.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.World;

import pl.north93.zgame.api.bukkit.tick.ITickable;
import pl.north93.zgame.api.bukkit.tick.ITickableManager;
import pl.north93.zgame.api.bukkit.tick.Tick;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;

public class ChunkLoadManager implements ITickable
{
    private static final int MIN_MEMORY = 196 * 1024 * 1024;
    
    private static final Logger logger = LogManager.getLogger();
    
    private final Map<String, ChunkLoadTask> tasks = new HashMap<>();
    
    private int skipTicks;
    
    @Bean
    private ChunkLoadManager(ITickableManager tickableManager)
    {
        tickableManager.addTickableObject(this);
    }
    
    public void cancelForWorld(World world)
    {
        ChunkLoadTask task = tasks.remove(world.getName());
        task.onCancelled();
    }
    
    public void start(ChunkLoadTask task)
    {
        tasks.put(task.getCallback().getWorld().getName(), task);
        task.onStarted();
    }
    
    @Tick
    private void handleTick()
    {
        if ( skipTicks > 0 )
        {
            skipTicks--;
            return;
        }
        
        if ( !serverHasEnoughMemory() )
        {
            logger.warn("Server doesn't have enough memory - skipping chunk loading");
            skipTicks = 20;
            System.gc();
            return;
        }
        
        Iterator<ChunkLoadTask> it = tasks.values().iterator();
        while ( it.hasNext() )
        {
            ChunkLoadTask task = it.next();
            
            task.tick();
            
            if ( task.isCompleted() )
            {
                task.onFinished();
                it.remove();
            }
        }
    }
    
    private boolean serverHasEnoughMemory()
    {
        return Runtime.getRuntime().freeMemory() >= MIN_MEMORY;
    }
}


