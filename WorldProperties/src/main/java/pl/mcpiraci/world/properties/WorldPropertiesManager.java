package pl.mcpiraci.world.properties;

import org.bukkit.World;
import org.bukkit.entity.Player;

public interface WorldPropertiesManager
{
    PropertiesConfig getServerConfig();
    
    PropertiesConfig getWorldConfig(String worldName);
    
    PropertiesConfig getWorldConfig(World world);
    
    WorldProperties getProperties(String worldName);
    
    WorldProperties getProperties(World world);
    
    PlayerProperties getPlayerProperties(String playerName);
    
    PlayerProperties getPlayerProperties(Player player);

    void reloadServerConfig();
}
