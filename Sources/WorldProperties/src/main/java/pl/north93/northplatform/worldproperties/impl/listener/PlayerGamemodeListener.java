package pl.north93.northplatform.worldproperties.impl.listener;

import static pl.north93.northplatform.api.bukkit.utils.nms.EntityTrackerHelper.toNmsPlayer;


import net.minecraft.server.v1_12_R1.EnumGamemode;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import pl.north93.northplatform.api.bukkit.server.AutoListener;
import pl.north93.northplatform.worldproperties.IPlayerProperties;
import pl.north93.northplatform.worldproperties.IWorldProperties;
import pl.north93.northplatform.worldproperties.IWorldPropertiesManager;

public class PlayerGamemodeListener implements AutoListener
{
    private final IWorldPropertiesManager propertiesManager;
    
    public PlayerGamemodeListener(IWorldPropertiesManager propertiesManager)
    {
        this.propertiesManager = propertiesManager;
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void setInitialGamemode(PlayerSpawnLocationEvent event)
    {
        IWorldProperties properties = propertiesManager.getProperties(event.getSpawnLocation().getWorld());
        GameMode gamemode = properties.getGamemode();
        EnumGamemode nmsGamemode = gamemode != null ? EnumGamemode.valueOf(gamemode.name()) : EnumGamemode.ADVENTURE;
        
        // We have to use NMS gamemode here because Bukkit doesn't allow gamemode change when playerConnection is null.
        toNmsPlayer(event.getPlayer()).playerInteractManager.setGameMode(nmsGamemode);
    }
    
    @EventHandler
    public void setGamemodeOnWorldChange(PlayerChangedWorldEvent event)
    {
        IWorldProperties properties = propertiesManager.getProperties(event.getPlayer().getWorld());
        IPlayerProperties playerProperties = propertiesManager.getPlayerProperties(event.getPlayer());
        
        if ( !playerProperties.canBypassRestrictions() && properties.getGamemode() != null )
        {
            event.getPlayer().setGameMode(properties.getGamemode());
        }
    }
}
