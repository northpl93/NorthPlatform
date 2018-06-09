package pl.mcpiraci.world.properties;

import javax.annotation.Nullable;

import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Location;
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
    @Nullable
    Weather getWeather();
    
    /**
     * @return true whenether block physics is enabled on that world.
     */
    boolean isPhysicsEnabled();
    
    /**
     * @return true whenether hunger is enabled on that world.
     */
    boolean isHungerEnabled();
    
    /**
     * @return true whenether mob can spawn on this world.
     */
    boolean isMobSpawningEnabled();
    
    /**
     * @return a immutable map of gamerules on that world
     */
    Map<String, String> getGamerules();
    
    /**
     * @return a gamemode that is aplied to the players on that world, null means no gamemode force
     */
    @Nullable
    GameMode getGamemode();

    /**
     * @return a location when players are spawned when teleporting to this world.
     */
    @Nullable
    Location getSpawn();

    /**
     * Reload config file located in world directory
     */
    void reloadWorldConfig();
}
