package pl.mcpiraci.world.properties.impl.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import lombok.extern.slf4j.Slf4j;
import pl.mcpiraci.world.properties.IPlayerProperties;
import pl.mcpiraci.world.properties.IWorldPropertiesManager;
import pl.north93.zgame.api.bukkit.utils.AutoListener;

@Slf4j
public class PlayerInvulnerableListener implements AutoListener
{
    private final IWorldPropertiesManager propertiesManager;
    
    private PlayerInvulnerableListener(IWorldPropertiesManager propertiesManager)
    {
        this.propertiesManager = propertiesManager;
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        makeFullHealthBar(event);
    }
    
    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent event)
    {
        makeFullHealthBar(event);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamage(EntityDamageEvent event)
    {
        if ( !( event.getEntity() instanceof Player ) )
        {
            return;
        }
        
        Player player = (Player) event.getEntity();
        IPlayerProperties playerProperties = propertiesManager.getPlayerProperties(player);
        
        if ( playerProperties.effectiveInvulnerable() )
        {
            event.setDamage(0.0);
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFallIntoVoid(PlayerMoveEvent event)
    {
        IPlayerProperties playerProperties = propertiesManager.getPlayerProperties(event.getPlayer());
        
        if ( event.getTo().getY() < 0 && playerProperties.effectiveInvulnerable() )
        {
            event.setTo(event.getPlayer().getWorld().getSpawnLocation());
        }
    }
    
    @EventHandler
    public void onPlayerDie(PlayerDeathEvent event)
    {
        IPlayerProperties playerProperties = propertiesManager.getPlayerProperties(event.getEntity());
        
        if ( playerProperties.effectiveInvulnerable() )
        {
            event.getEntity().spigot().respawn();
            log.warn("Player {} died even though he was invulnerable", event.getEntity());
        }
    }
    
    @SuppressWarnings("deprecation")
    private void makeFullHealthBar(PlayerEvent event)
    {
        IPlayerProperties playerProperties = propertiesManager.getPlayerProperties(event.getPlayer());
        
        if ( !playerProperties.effectiveInvulnerable() )
        {
            event.getPlayer().setHealth(event.getPlayer().getMaxHealth());
        }
    }
}
