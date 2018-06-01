package pl.mcpiraci.world.properties.impl.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import pl.mcpiraci.world.properties.IPlayerProperties;
import pl.mcpiraci.world.properties.IWorldPropertiesManager;
import pl.north93.zgame.api.bukkit.utils.AutoListener;

public class PlayerInvulnerableListener implements AutoListener
{
    private static final Logger logger = LogManager.getLogger();
    
    private final IWorldPropertiesManager propertiesManager;
    
    private PlayerInvulnerableListener(IWorldPropertiesManager propertiesManager)
    {
        this.propertiesManager = propertiesManager;
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
            logger.warn("Player {} died even though he was invulnerable", event.getEntity());
        }
    }
}
