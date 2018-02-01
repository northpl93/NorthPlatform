package pl.arieals.minigame.goldhunter;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.BlockVector;

import com.google.common.base.Preconditions;

import pl.north93.zgame.api.bukkit.tick.ITickable;
import pl.north93.zgame.api.global.utils.lang.CatchException;

public abstract class StructureEntity implements ITickable
{
    private StructureEntityManager manager;
    
    protected final StructureBuilder structureBuilder()
    {
        return new StructureBuilder();
    }
    
    protected final StructureBuilder structureBuilder(BlockVector location, Material type, int data)
    {
        Preconditions.checkArgument(isSpawned(), "Entity must be spawned to place block.");
        
        return new StructureBuilder().and(location, type, data);
    }
    
    final boolean onSpawn(StructureEntityManager manager)
    {
        this.manager = manager;
        
        boolean result = false;
        try 
        {
            result = trySpawn();
        }
        catch ( Throwable e )
        {
            System.err.println("trySpawn() throws an exception");
            e.printStackTrace();
        }
        
        if ( !result )
        {
            manager = null;
        }
        
        return result;
    }
    
    final void onRemove(StructureEntityManager manager)
    {
        Preconditions.checkState(this.manager != null);
        manager = null;
        
        CatchException.printStackTrace(this::onDespawn, "onDespawn() throws an exception");
    }
    
    public final void destroy()
    {
        Preconditions.checkState(isSpawned(), "Entity isn't spawned");
        manager.remove(this);
    }
    
    public final boolean isSpawned()
    {
        return manager != null;
    }
    
    public final GoldHunterArena getArena()
    {
        Preconditions.checkState(isSpawned(), "Entity isn't spawned");
        return manager.getArena();
    }
    
    public final World getWorld()
    {
        Preconditions.checkState(isSpawned(), "Entity isn't spawned");
        return manager.getArena().getLocalArena().getWorld().getCurrentWorld();
    }
    
    protected boolean trySpawn()
    {
        return true;
    }
    
    protected void onDespawn()
    {
        
    }
    
    protected boolean onBlockDestroy(BlockVector location, GoldHunterPlayer destroyer)
    {
        return true;
    }
    
    @Override
    public final boolean equals(Object obj)
    {
        return super.equals(obj);
    }
    
    @Override
    public final int hashCode()
    {
        return super.hashCode();
    }
    
    @Override
    public String toString()
    {
        return new ToStringBuilder(this).append("arena", manager.getArena().getLogger().getName()).build();
    }
    
    protected final class StructureBuilder
    {
        private final Map<BlockVector, StructureBuilderEntry> entries = new HashMap<>();
        
        StructureBuilder and(BlockVector location, Material type, int data)
        {
            entries.put(location, new StructureBuilderEntry(type, data));
            return this;
        }
        
        boolean tryBuild()
        {
            Preconditions.checkState(manager != null);
            
            for ( BlockVector location : entries.keySet() )
            {
                if ( manager.hasBlockEntity(location) )
                {
                    return false;
                }
            }
            
            entries.entrySet().forEach(e -> manager.placeBlock(StructureEntity.this, e.getKey(), e.getValue().type, e.getValue().data));
            return true;
        }
    }
    
    private static class StructureBuilderEntry
    {
        private final Material type;
        private final int data;
        
        private StructureBuilderEntry(Material type, int data)
        {
            this.type = type;
            this.data = data;
        }
    }
}
