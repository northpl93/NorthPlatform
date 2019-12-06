package pl.arieals.minigame.goldhunter.listener;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.northplatform.api.bukkit.gui.event.GuiOpenEvent;
import pl.north93.northplatform.api.bukkit.utils.AutoListener;

public class GameplayRestrictionListener implements AutoListener
{
    private static final List<InventoryType> ALLOWED_CONTAINERS = Arrays.asList(InventoryType.CRAFTING, InventoryType.WORKBENCH, InventoryType.PLAYER, InventoryType.CREATIVE);
    private static final List<Material> CRAFTABLE_MATERIALS = Arrays.asList(Material.STICK, Material.LADDER, Material.WOOD_PLATE, Material.STEP, Material.WORKBENCH);
            
    private final GoldHunter goldHunter;
    
    public GameplayRestrictionListener(GoldHunter goldHunter)
    {
        this.goldHunter = goldHunter;
    }
    
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event)
    {
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onRegainHealth(EntityRegainHealthEvent event)
    {
        if ( event.getRegainReason() == RegainReason.EATING )
        {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onContainerOpen(InventoryOpenEvent event)
    {
        InventoryType type = event.getInventory().getType();
        
        if ( event instanceof GuiOpenEvent )
        {
            return;
        }
        
        if ( goldHunter.getPlayer(event.getPlayer()) != null && !ALLOWED_CONTAINERS.contains(type) )
        {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onCraftPrepare(PrepareItemCraftEvent event)
    {   
        if ( event.getRecipe() == null )
        {
            return;
        }
        
        Material material = event.getRecipe().getResult().getType();
        
        GoldHunterPlayer player = goldHunter.getPlayer(event.getView().getPlayer());
        
        if ( player != null && !CRAFTABLE_MATERIALS.contains(material) )
        {
            event.getInventory().setResult(null);
            event.getViewers().forEach(p -> ((Player) p).updateInventory());
        }
    }
}
