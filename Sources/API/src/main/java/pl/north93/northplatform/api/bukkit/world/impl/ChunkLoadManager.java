package pl.north93.northplatform.api.bukkit.world.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.World;

import org.apache.commons.io.FileUtils;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.bukkit.tick.ITickable;
import pl.north93.northplatform.api.bukkit.tick.ITickableManager;
import pl.north93.northplatform.api.bukkit.tick.Tick;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;

@Slf4j
/*default*/ class ChunkLoadManager implements ITickable
{
    private static final long MIN_MEMORY = calculateMinMemory();

    private final Map<String, ChunkLoadTask> tasks = new HashMap<>();
    
    private int skipTicks;
    
    @Bean
    private ChunkLoadManager(ITickableManager tickableManager)
    {
        tickableManager.addTickableObject(this);

        final String maxMemory = FileUtils.byteCountToDisplaySize(Runtime.getRuntime().maxMemory());
        final String minMemory = FileUtils.byteCountToDisplaySize(MIN_MEMORY);
        log.info("Max server memory: {}, free memory target {}", maxMemory, minMemory);
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

    private static long calculateMinMemory()
    {
        final double freeMemoryPercent = 0.1;
        return (long) (Runtime.getRuntime().maxMemory() * freeMemoryPercent);
    }
}


