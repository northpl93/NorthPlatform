package pl.arieals.minigame.goldhunter.player;

import java.util.HashMap;

import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Preconditions;

import net.minecraft.server.v1_12_R1.MinecraftServer;

import pl.arieals.minigame.goldhunter.GoldHunterLogger;
import pl.arieals.minigame.goldhunter.classes.xml.XmlInventoryRefilRule;
import pl.arieals.minigame.goldhunter.utils.ItemStackUtils;
import pl.north93.zgame.api.bukkit.tick.ITickable;
import pl.north93.zgame.api.bukkit.tick.Tick;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class InventoryRefilTracker implements ITickable
{
    @Inject
    @GoldHunterLogger
    private static Logger logger;
    
    private final GoldHunterPlayer player;
    
    public InventoryRefilTracker(GoldHunterPlayer player)
    {
        this.player = player;
    }
    
    @Tick
    private void tick()
    {
        if ( player.isIngame() )
        {
            tryRefilEquipment();
        }
    }
    
    private void tryRefilEquipment()
    {
        for ( XmlInventoryRefilRule refilRule : player.getCurrentClass().getInventoryRefilRules() )
        {
            if ( MinecraftServer.currentTick % refilRule.getPeriod() == 0 )
            {
                tryRefilItem(refilRule.createItemStack(), refilRule.getMaxCount());
            }
        }
    }
    
    private void tryRefilItem(ItemStack is, int maxCount)
    {
        Player player = this.player.getPlayer();
        
        int currentCount = 0;
        
        ItemStackUtils.hideAttributesAndMakeUnbreakable(is);
        
        InventoryView openInventory = player.getOpenInventory();
        if ( openInventory.getCursor() != null && openInventory.getCursor().isSimilar(is) )
        {
            currentCount += openInventory.getCursor().getAmount();
        }
        
        for ( ItemStack item : openInventory.getTopInventory().getContents() )
        {
            if ( item != null && item.isSimilar(is) )
            {
                currentCount += item.getAmount();
            }
        }
        
        for ( ItemStack item : openInventory.getBottomInventory().getContents() )
        {
            if ( item != null && item.isSimilar(is) )
            {
                currentCount += item.getAmount();
            }
        }
        
        if ( currentCount >= maxCount )
        {
            System.out.println("current count: " + currentCount);
            return;
        }
        
        int diff = maxCount - currentCount;
        is.setAmount(Math.min(diff, is.getAmount()));

        addItem(is);
    }
    
    private void addItem(ItemStack is)
    {
        Player player = this.player.getPlayer();
        
        for ( ItemStack itemstack : player.getInventory().getContents() )
        {
            if ( itemstack != null && itemstack.isSimilar(is) )
            {
                int freeAmount = 64 - itemstack.getAmount();
                int toAdd = Math.min(freeAmount, is.getAmount());
                
                itemstack.setAmount(itemstack.getAmount() + toAdd);
                
                int rest = is.getAmount() - toAdd;
                if ( rest == 0 )
                {
                    return;
                }
                
                is.setAmount(rest);
            }
        }
        
        Preconditions.checkState(is.getAmount() > 0);
        player.getInventory().addItem(is);
    }
    
    
}
