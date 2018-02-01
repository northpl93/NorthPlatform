package pl.arieals.minigame.goldhunter;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import pl.north93.zgame.api.bukkit.tick.ITickableManager;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class StructureEntityManager
{
    @Inject
    @GoldHunterLogger
    private static Logger logger;
    
    @Inject
    private static ITickableManager tickableManager;
    
    private final Collection<StructureEntity> entities = new HashSet<>();
    
    private final Multimap<StructureEntity, BlockVector> blocksByEntity = ArrayListMultimap.create();
    private final Map<BlockVector, StructureEntity> entitiesByBlock = new HashMap<>();
    
    private final GoldHunterArena arena;
    
    public StructureEntityManager(GoldHunterArena arena)
    {
        this.arena = arena;
        tickableManager.addTickableObjectsCollection(entities);
    }
    
    public GoldHunterArena getArena()
    {
        return arena;
    }
    
    public boolean spawn(StructureEntity entity)
    {
        Preconditions.checkState(entities.contains(entity), "Cannot add entity twice");
        
        if ( entity.onSpawn(this) )
        {
            entities.add(entity);
            logger.debug("Added structure entity {} on arena {}", entity, arena);
            return true;
        }
        
        logger.debug("Failed to add structure entity {} on arena {}", entity, arena);
        return false;
    }
    
    @SuppressWarnings("deprecation")
    public void remove(StructureEntity entity)
    {
        Preconditions.checkState(entities.remove(entity), "Entity isn't spawned");
        logger.debug("Removed structure entity {} on arena {}", entity, arena);
        
        for ( BlockVector loc : blocksByEntity.get(entity) )
        {
            entity.getWorld().getBlockAt(loc.toLocation(entity.getWorld())).setTypeIdAndData(0, (byte) 0, false);
        }
        
        entity.onRemove(this);
    }
    
    public StructureEntity getEntityForBlock(BlockVector location)
    {
        return entitiesByBlock.get(location);
    }
    
    public boolean hasBlockEntity(BlockVector location)
    {
        return getEntityForBlock(location) != null;
    }
    
    @SuppressWarnings("deprecation")
    void placeBlock(StructureEntity entity, BlockVector location, Material type, int data)
    {
        Preconditions.checkState(entitiesByBlock.containsKey(location));
        
        World world = arena.getLocalArena().getWorld().getCurrentWorld();
        Block block = world.getBlockAt(location.toLocation(world));
        block.setType(type);
        block.setData((byte) data);
        
        entitiesByBlock.put(location, entity);
        blocksByEntity.put(entity, location);
    }
}
