package pl.arieals.minigame.goldhunter.player;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.v1_12_R1.EntityPotion;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

import com.google.common.base.Preconditions;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftThrownPotion;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ThrownPotion;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.north93.zgame.api.bukkit.utils.nms.NbtTagType;
import pl.north93.zgame.api.global.component.annotations.bean.Aggregator;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;

@Slf4j
public class PotionManager
{
    private final Map<String, PotionHandler> potionsHandlers = new HashMap<>();
    
    private final GoldHunter goldHunter;
    
    @Bean
    private PotionManager(GoldHunter goldHunter)
    {
        this.goldHunter = goldHunter;
    }
    
    public void splashPotion(GoldHunterPlayer shooter, ThrownPotion potion)
    {
        // We must use EntityPotion#getItem() here, because ThrownPotion#getItem() doesn't return a CraftItemStack 
        // but an ordinary Bukkit's ItemStack which cannot hold nbt data
        EntityPotion entityPotion = ((CraftThrownPotion) potion).getHandle();
        NBTTagCompound potionData = entityPotion.getItem().d("goldhunter:potion");
        
        log.debug("Splash potion - shooter: {}, data: {}", shooter, potionData);
        
        if ( potionData == null )
        {
            return;
        }
        
        for ( Entity entity : potion.getNearbyEntities(3, 1.8, 3) )
        {
            GoldHunterPlayer target = goldHunter.getPlayer(entity);
            
            if ( target == null )
            {
                continue;
            }
            
            try
            {
                applyPotionEffect(shooter, target, potionData);
            }
            catch ( Exception e )
            {
                log.error("Couldn't apply potion for player {} with data {}", target, potionData, e);
            }
        }
    }
    
    public boolean applyPotionEffect(@NonNull GoldHunterPlayer shooter, @NonNull GoldHunterPlayer target, NBTTagCompound potionData)
    {
        log.debug("Apply potion effect for {} with {}", target, potionData);
        Preconditions.checkArgument(potionData.hasKeyOfType("type", NbtTagType.TAG_STRING), "Invalid potion data (type key does not exist)");
        
        String type = potionData.getString("type");
        PotionHandler potionHandler = getPotionHandler(type);
        
        if ( potionHandler == null )
        {
            throw new IllegalStateException("Potion handler for type'" + type + "' doesn't exist");
        }

        return potionHandler.applyPotionEffect(shooter, target, potionData);
    }
    
    public PotionHandler getPotionHandler(String name)
    {
        return potionsHandlers.get(name);
    }
    
    @Aggregator(PotionHandler.class)
    public void agreggatePotionHandlers(PotionHandler handler)
    {
        log.debug("Aggregate potion handler class: {}", handler.getClass());
        
        String handlerName = handler.getClass().getSimpleName();
        
        PotionHandler current = potionsHandlers.putIfAbsent(handlerName, handler);
        if ( current != null )
        {
            log.error("Cannot register potion handler class {}: name {} is already occupied by {}",
                    handler.getClass().getName(), handlerName, current.getClass().getName());
            
            return;
        }
    }
}
