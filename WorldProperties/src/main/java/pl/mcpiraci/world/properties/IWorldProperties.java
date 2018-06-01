package pl.mcpiraci.world.properties;

import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.World;

public interface IWorldProperties
{
    /**
     * @return a world that properties are applied
     */
    World getWorld();
    
    /**
     * @return a properties config fot that world
     */
    PropertiesConfig getWorldConfig();
    
    /**
     * @return true whenether players are allowed to build on that world.
     */
    boolean isBuildAllowed();
    
    /**
     * @return true whenether players are allowed to interract with block or entities.
     */
    boolean isInterractAllowed();
    
    /**
     * @return true whenether players are invulnerable on that world.
     */
    boolean arePlayersInvulnerable();
    
    /**
     * @return a fixed time value on world, -1 means that time updates naturally.
     */
    int getTime();
    
    /**
     * @return a fixed weather on world, null means naturally weather cycle.
     */
    Weather getWeather();
    
    /**
     * @return true whenether block physics is enabled on that world.
     */
    boolean isPhysicsEnabled();
    /**
     * @return a immutable map of gamerules on that world
     */
    Map<String, String> getGamerules();
    
    /**
     * @return a gamemode that is aplied to the players on that world, null means no gamemode force
     */
    GameMode getGamemode();
    
    /**
     * Reload config file located in world directory
     */
    void reloadWorldConfig();
}
