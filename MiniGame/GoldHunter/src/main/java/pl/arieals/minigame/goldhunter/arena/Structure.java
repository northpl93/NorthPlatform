package pl.arieals.minigame.goldhunter.arena;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import org.apache.commons.lang3.builder.ToStringBuilder;

import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.zgame.api.bukkit.tick.ITickable;
import pl.north93.zgame.api.global.utils.lang.CatchException;

public abstract class Structure implements ITickable
{
    private StructureManager manager;
    private boolean spawnPhase;
    
    private final BlockVector baseLocation;
    
    protected Structure(BlockVector baseLocation)
    {
        Preconditions.checkNotNull(baseLocation);
        this.baseLocation = baseLocation;
    }
    
    public BlockVector getBaseLocation()
    {
        return baseLocation.clone();
    }
    
    protected final StructureBuilder structureBuilder()
    {
        Preconditions.checkArgument(spawnPhase, "Structure must be in spawn phase to build a structure.");
        return new StructureBuilder();
    }
    
    protected final StructureBuilder structureBuilder(int offsetX, int offsetY, int offsetZ, Material type)
    {
        return new StructureBuilder().and(offsetX, offsetY, offsetZ, type, 0);
    }
    
    protected final StructureBuilder structureBuilder(int offsetX, int offsetY, int offsetZ, Material type, int data)
    {
        return new StructureBuilder().and(offsetX, offsetY, offsetZ, type, data);
    }
    
    protected final StructureBuilder structureBuilder(BlockVector location, Material type)
    {
        return structureBuilder(location, type, 0);
    }
    
    protected final StructureBuilder structureBuilder(BlockVector location, Material type, int data)
    {
        Preconditions.checkArgument(spawnPhase, "Structure must be in spawn phase to build a structure.");
        return new StructureBuilder().and(location, type, data);
    }
    
    final boolean onSpawn(StructureManager manager)
    {
        this.manager = manager;
        
        boolean result = false;
        try 
        {
            spawnPhase = true;
            result = trySpawn();
        }
        catch ( Throwable e )
        {
            System.err.println("trySpawn() throws an exception");
            e.printStackTrace();
        }
        finally
        {
            spawnPhase = false;
        }
        
        if ( !result )
        {
            manager = null;
        }
        
        return result;
    }
    
    final void onRemove(StructureManager manager)
    {
        Preconditions.checkState(this.manager != null);
        manager = null;
        
        CatchException.printStackTrace(this::onRemove, "onDespawn() throws an exception");
    }
    
    final void callOnDestroy(GoldHunterPlayer destroyer)
    {
        Preconditions.checkState(this.manager != null);
        CatchException.printStackTrace(() -> onDestroy(destroyer), "onDestroy throws an exception");
    }
    
    public final void removeStructure()
    {
        Preconditions.checkState(isSpawned(), "Structure isn't spawned");
        manager.remove(this);
    }
    
    public final boolean isSpawned()
    {
        return manager != null;
    }
    
    public final GoldHunterArena getArena()
    {
        Preconditions.checkState(isSpawned(), "Structure isn't spawned");
        return manager.getArena();
    }
    
    public final World getWorld()
    {
        Preconditions.checkState(isSpawned(), "Structure isn't spawned");
        return manager.getArena().getLocalArena().getWorld().getCurrentWorld();
    }
    
    protected boolean trySpawn()
    {
        return true;
    }
    
    protected void onRemove()
    {
        
    }
    
    protected void onDestroy(GoldHunterPlayer destroyer)
    {
        
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
        return new ToStringBuilder(this).append("arena", manager.getArena().getLocalArena().getId()).build();
    }
    
    public final class StructureBuilder
    {
        private final Map<BlockVector, StructureBuilderEntry> entries = new HashMap<>();
        
        public StructureBuilder and(int offsetX, int offsetY, int offsetZ, Material type)
        {
            return and(getBaseLocation().add(new Vector(offsetX, offsetY, offsetZ)).toBlockVector(), type, 0);
        }
        
        public StructureBuilder and(int offsetX, int offsetY, int offsetZ, Material type, int data)
        {
            return and(getBaseLocation().add(new Vector(offsetX, offsetY, offsetZ)).toBlockVector(), type, data);
        }
        
        public StructureBuilder and(BlockVector location, Material type)
        {
            return and(location, type, 0);
        }
        
        public StructureBuilder and(BlockVector location, Material type, int data)
        {
            entries.put(location, new StructureBuilderEntry(type, data));
            return this;
        }
        
        public boolean tryBuild()
        {
            Preconditions.checkState(manager != null);
            
            for ( BlockVector location : entries.keySet() )
            {
                if ( !manager.canBuildStructure(location) )
                {
                    return false;
                }
            }
            
            entries.entrySet().forEach(e -> manager.buildStructureBlock(Structure.this, e.getKey(), e.getValue().type, e.getValue().data));
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
