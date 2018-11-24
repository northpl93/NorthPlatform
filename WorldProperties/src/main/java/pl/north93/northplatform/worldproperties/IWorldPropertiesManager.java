package pl.north93.northplatform.worldproperties;

import org.bukkit.World;
import org.bukkit.entity.Player;

public interface IWorldPropertiesManager
{
    PropertiesConfig getServerConfig();
    
    PropertiesConfig getWorldConfig(String worldName);
    
    PropertiesConfig getWorldConfig(World world);
    
    IWorldProperties getProperties(String worldName);
    
    IWorldProperties getProperties(World world);
    
    IPlayerProperties getPlayerProperties(String playerName);
    
    IPlayerProperties getPlayerProperties(Player player);

    void reloadServerConfig();
}
