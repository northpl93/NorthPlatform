package pl.north93.northplatform.minigame.goldhunter.listener;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import pl.north93.northplatform.minigame.goldhunter.GoldHunter;
import pl.north93.northplatform.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.northplatform.api.bukkit.server.AutoListener;

public class AdditionalGameplayMechanicsListener implements AutoListener
{
    private final GoldHunter goldHunter;
    
    public AdditionalGameplayMechanicsListener(GoldHunter goldHunter)
    {
        this.goldHunter = goldHunter;
    }
    
    @EventHandler
    public void pushPlayerWhenCaughtByFishRod(PlayerFishEvent event)
    {
        if ( event.getState() != State.CAUGHT_ENTITY )
        {
            return;
        }
        
        GoldHunterPlayer player = goldHunter.getPlayer(event.getPlayer());
        Entity caught = event.getCaught();
        
        if ( caught instanceof Player )
        {
            GoldHunterPlayer caughtPlayer = goldHunter.getPlayer((Player) caught);
            if ( player.getTeam() == caughtPlayer.getTeam() )
            {
                return;
            }
        }
        
        if ( player != null && caught != null )
        {
            Vector toCatcher = player.getPlayer().getLocation().subtract(caught.getLocation()).toVector();
            caught.setVelocity(toCatcher.normalize().setY(0).multiply(0.7454).setY(0.569));
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void everySolidBlockDropPlanks(BlockBreakEvent event)
    {
        GoldHunterPlayer player = goldHunter.getPlayer(event.getPlayer());
        
        if ( player != null )
        {
            event.setExpToDrop(0);
            event.setDropItems(false);
            
            if ( event.getBlock().getType().isSolid() )
            {
                event.getBlock().getWorld().dropItem(event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.WOOD));
            }
        }
    }
}
