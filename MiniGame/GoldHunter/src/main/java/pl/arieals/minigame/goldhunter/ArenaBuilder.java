package pl.arieals.minigame.goldhunter;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.google.common.base.Preconditions;

import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.utils.Result;

public class ArenaBuilder
{
    private final GoldHunter goldHunter;
    
    @Bean
    public ArenaBuilder(GoldHunter goldHunter)
    {
        this.goldHunter = goldHunter;
    }
    
    public Result tryDestroy(Location loc)
    {
        return tryBuild(loc.getBlock(), Material.AIR, 0);
    }
    
    public Result tryBuild(Location loc, Material type)
    {
        return tryBuild(loc.getBlock(), type, 0);
    }
    
    public Result tryBuild(Location loc, Material type, int data)
    {
        return tryBuild(loc.getBlock(), type, data);
    }
    
    public Result tryDestroy(Block block)
    {
        return tryBuild(block, Material.AIR, 0);
    }
    
    public Result tryBuild(Block block, Material type)
    {
        return tryBuild(block, type, 0);
    }
    
    @SuppressWarnings("deprecation")
    public Result tryBuild(Block block, Material type, int data)
    {
        GoldHunterArena arena = goldHunter.getArenaForWorld(block.getWorld());
        Preconditions.checkState(arena != null, "Cannot use MapBuilder on non-arena world");
        
        if ( !arena.canBuild(block.getLocation()) )
        {
            return Result.FAILTURE;
        }
        
        block.setTypeIdAndData(type.getId(), (byte) data, false);
        return Result.SUCCESS;
    }
}
