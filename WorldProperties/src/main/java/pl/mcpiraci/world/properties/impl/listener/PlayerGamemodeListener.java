package pl.mcpiraci.world.properties.impl.listener;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import net.minecraft.server.v1_12_R1.EnumGamemode;

import pl.mcpiraci.world.properties.IWorldProperties;
import pl.mcpiraci.world.properties.IWorldPropertiesManager;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.bukkit.utils.AutoListener;

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
        INorthPlayer.asCraftPlayer(event.getPlayer()).getHandle().playerInteractManager.setGameMode(nmsGamemode);
    }
    
    @EventHandler
    public void setGamemodeOnWorldChange(PlayerChangedWorldEvent event)
    {
        IWorldProperties properties = propertiesManager.getProperties(event.getPlayer().getWorld());
        
        if ( properties.getGamemode() != null )
        {
            event.getPlayer().setGameMode(properties.getGamemode());
        }
    }
}
