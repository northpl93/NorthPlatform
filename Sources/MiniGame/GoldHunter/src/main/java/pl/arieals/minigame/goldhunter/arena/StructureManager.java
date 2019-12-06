package pl.arieals.minigame.goldhunter.arena;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;

import org.slf4j.Logger;

import pl.arieals.minigame.goldhunter.GoldHunterLogger;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.northplatform.api.bukkit.tick.ITickableManager;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

public class StructureManager
{
    @Inject
    @GoldHunterLogger
    private static Logger logger;
    
    @Inject
    private static ITickableManager tickableManager;
    
    private final Collection<Structure> entities = new HashSet<>();
    
    private final Multimap<Structure, BlockVector> blocksByEntity = ArrayListMultimap.create();
    private final Map<BlockVector, Structure> entitiesByBlock = new HashMap<>();
    
    private final GoldHunterArena arena;
    
    public StructureManager(GoldHunterArena arena)
    {
        this.arena = arena;
        tickableManager.addTickableObjectsCollection(entities);
    }
    
    public GoldHunterArena getArena()
    {
        return arena;
    }
    
    public boolean spawn(Structure structure)
    {
        Preconditions.checkState(!entities.contains(structure), "Cannot add entity twice");
        
        if ( structure.onSpawn(this) )
        {
            // XXX: Should we chceck for at least one block has been placed by entity?
            
            entities.add(structure);
            logger.debug("Added structure {} on arena {}", structure, arena);
            return true;
        }
        
        logger.debug("Failed to add structure {} on arena {}", structure, arena);
        return false;
    }
    
    @SuppressWarnings("deprecation")
    public void remove(Structure entity)
    {
        Preconditions.checkState(entities.remove(entity), "Entity isn't spawned");
        logger.debug("Removed structure entity {} on arena {}", entity, arena);
        
        for ( BlockVector loc : blocksByEntity.removeAll(entity) )
        {
            entity.getWorld().getBlockAt(loc.toLocation(entity.getWorld())).setTypeIdAndData(0, (byte) 0, false);
            entitiesByBlock.remove(loc);
        }
        
        entity.onRemove(this);
    }
    
    public void clearStructures()
    {
        logger.debug("Clear structures for arena {}", arena);
        
        entities.clear();
        entitiesByBlock.clear();
        blocksByEntity.clear();
    }
    
    public boolean tryDestroyStructure(GoldHunterPlayer destroyer, Location location)
    {
        Structure structure = getStructure(location);
        
        if ( structure == null )
        {
            return false;
        }
        
        logger.debug("Destroy structer on {} by {}", location, destroyer);
        
        structure.callOnDestroy(destroyer);
        return true;
    }
    
    public Structure getStructure(Location location)
    {
        Preconditions.checkArgument(location.getWorld().equals(arena.getCurrentWorld()));
        return getStructure(location.toVector().toBlockVector());
    }
    
    public Structure getStructure(BlockVector location)
    {
        return entitiesByBlock.get(location);
    }
    
    public boolean isStructure(Location location)
    {
        return getStructure(location) != null;
    }
    
    public boolean isStructure(BlockVector location)
    {
        return getStructure(location) != null;
    }
    
    public Collection<Structure> getStructures()
    {
        return new HashSet<>(entities);
    }
    
    public <T extends Structure> Collection<T> getStructuresOfType(final Class<T> type)
    {
        return entities.stream().filter(e -> type.isInstance(e)).map(e -> type.cast(e)).collect(Collectors.toCollection(HashSet::new));
    }
    
    boolean canBuildStructure(BlockVector location)
    {
        return !isStructure(location) && arena.canBuild(location);
    }
    
    @SuppressWarnings("deprecation")
    void buildStructureBlock(Structure entity, BlockVector location, Material type, int data)
    {
        Preconditions.checkState(!entitiesByBlock.containsKey(location), "Cannot place block where is StructureEntity");
        
        World world = arena.getLocalArena().getWorld().getCurrentWorld();
        Block block = world.getBlockAt(location.toLocation(world));
        block.setType(type);
        block.setData((byte) data);
        
        entitiesByBlock.put(location, entity);
        blocksByEntity.put(entity, location);
    }
}
