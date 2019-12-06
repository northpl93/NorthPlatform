package pl.arieals.minigame.goldhunter.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import org.slf4j.Logger;

import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.GoldHunterLogger;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.northplatform.api.bukkit.utils.AutoListener;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

public class PlayerDeathListener implements AutoListener
{
    @Inject
    @GoldHunterLogger
    private Logger logger;
    
    @Inject
    private GoldHunter goldHunter;
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDieInVoid(PlayerMoveEvent event)
    {
        GoldHunterPlayer player = goldHunter.getPlayer(event.getPlayer());
        if ( player == null )
        {
            return;
        }
        
        if ( event.getTo().getY() < 0 )
        {
            player.die();
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDie(EntityDamageEvent event)
    {
        if ( !( event.getEntity() instanceof Player) )
        {
            return;
        }
        
        GoldHunterPlayer player = goldHunter.getPlayer((Player) event.getEntity());
        if ( player == null )
        {
            return;
        }
        
        if ( event.getFinalDamage() >= player.getPlayer().getHealth() )
        {
            if ( player.getPlayer().getHealth() > 0.00001 )
            {
                event.setDamage(0.00001);
            }
            else
            {
                event.setCancelled(true);
            }
            
            player.die();
        }
    }
    
    @EventHandler
    public void onPlayerDie(PlayerDeathEvent event)
    {
        // THIS NEVER SOHULD BE CALLED
        logger.warn("PlayerDeathEvent was called for player {}", event.getEntity().getName());
        
        event.setDeathMessage(null);
        event.setDroppedExp(0);
        event.setKeepInventory(true);
        event.setKeepLevel(true);
        
        goldHunter.runTask(event.getEntity().spigot()::respawn);
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        GoldHunterPlayer player = goldHunter.getPlayer(event.getPlayer());
        
        if ( player.isIngame() )
        {
            event.setRespawnLocation(player.respawn());
        }
        else
        {
            event.setRespawnLocation(Bukkit.getWorlds().get(0).getSpawnLocation());
        }
    }
}
