package pl.north93.zgame.api.bukkit.world.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.World;

import lombok.extern.slf4j.Slf4j;
import pl.north93.zgame.api.bukkit.tick.ITickable;
import pl.north93.zgame.api.bukkit.tick.ITickableManager;
import pl.north93.zgame.api.bukkit.tick.Tick;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;

@Slf4j
public class ChunkLoadManager implements ITickable
{
    private static final int MIN_MEMORY = 128 * 1024 * 1024;

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
        if ( skipTicks > 0 || tasks.isEmpty() )
        {
            skipTicks--;
            return;
        }

        if ( !serverHasEnoughMemory() )
        {
            log.warn("Server doesn't have enough memory - skipping chunk loading");
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


