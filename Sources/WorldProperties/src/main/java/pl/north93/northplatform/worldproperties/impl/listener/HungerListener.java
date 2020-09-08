package pl.north93.northplatform.worldproperties.impl.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import pl.north93.northplatform.api.bukkit.server.AutoListener;
import pl.north93.northplatform.worldproperties.IPlayerProperties;
import pl.north93.northplatform.worldproperties.IWorldPropertiesManager;

public class HungerListener implements AutoListener
{
    private final IWorldPropertiesManager propertiesManager;
    
    private HungerListener(IWorldPropertiesManager propertiesManager)
    {
        this.propertiesManager = propertiesManager;
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        makeFullHungerBar(event);
    }
    
    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent event)
    {
        makeFullHungerBar(event);
    }
    
    private void makeFullHungerBar(PlayerEvent event)
    {
        IPlayerProperties playerProperties = propertiesManager.getPlayerProperties(event.getPlayer());
        
        if ( !playerProperties.effectiveHunger() )
        {
            event.getPlayer().setFoodLevel(20);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFoodLevelChange(FoodLevelChangeEvent event)
    {
        IPlayerProperties playerProperties = propertiesManager.getPlayerProperties((Player) event.getEntity());
        
        if ( !playerProperties.effectiveHunger() )
        {
            event.setCancelled(true);
        }
    }
}
