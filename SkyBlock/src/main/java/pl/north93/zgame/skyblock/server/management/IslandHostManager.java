package pl.north93.zgame.skyblock.server.management;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.skyblock.api.IIslandHostManager;
import pl.north93.zgame.skyblock.api.IslandData;
import pl.north93.zgame.skyblock.api.cfg.IslandConfig;
import pl.north93.zgame.skyblock.api.utils.Coords2D;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyblock.server.world.WorldManager;

/**
 * Klasa zarządzająca serwerem pracującym w trybie hosta wysp.
 */
public class IslandHostManager implements ISkyBlockServerManager, IIslandHostManager
{
    @InjectComponent("SkyBlock.Server")
    private SkyBlockServer     skyBlockServer;
    @InjectComponent("API.Database.Redis.RPC")
    private IRpcManager        rpcManager;
    private Logger             logger;
    private List<WorldManager> worldManagers;

    @Override
    public void start()
    {
        this.worldManagers = new ArrayList<>(4);
        this.logger.info("[SkyBlock] Setting up world managers...");
        this.skyBlockServer.getSkyBlockConfig().getIslandTypes().forEach(this::setupWorldFor);
        this.rpcManager.addRpcImplementation(IIslandHostManager.class, this);
        this.logger.info("[SkyBlock] SkyBlock host manager initialised successfully.");
    }

    private void setupWorldFor(final IslandConfig islandConfig)
    {
        this.logger.info("[SkyBlock] Setting up world for " + islandConfig.getName() + "...");
        final String worldName = "sky_" + islandConfig.getName();

        final WorldCreator worldCreator = new WorldCreator(worldName);
        worldCreator.environment(World.Environment.NORMAL);
        worldCreator.generatorSettings("0");
        worldCreator.type(WorldType.FLAT);
        worldCreator.generateStructures(false);
        final World world = Bukkit.createWorld(worldCreator);

        this.logger.info("[SkyBlock] World " + worldName + " is ready!");

        this.logger.info("[SkyBlock] Creating world manager...");
        final WorldManager manager = new WorldManager(islandConfig, world);
        manager.init();
        this.worldManagers.add(manager);
    }

    @Override
    public void stop()
    {
    }

    /**
     * Zwraca menadżera światów dla danego typu wyspy.
     *
     * @param islandType nazwa typu wyspy.
     * @return menadżer światów.
     */
    private WorldManager getWorldManager(final String islandType)
    {
        for (final WorldManager worldManager : this.worldManagers)
        {
            if (islandType.equals(worldManager.getIslandConfig().getName()))
            {
                return worldManager;
            }
        }
       throw new IllegalArgumentException("Can't find WorldManager for " + islandType);
    }

    @Override
    public Integer getIslands()
    {
        return this.worldManagers.stream().mapToInt(WorldManager::getIslandsCount).sum();
    }

    @Override
    public Coords2D getFirstFreeLocation(final String islandType)
    {
        return this.getWorldManager(islandType).getFirstFreeLocation();
    }

    @Override
    public void islandAdded(final IslandData islandData)
    {
        this.logger.info("[SkyBlock] Received info about new island: " + islandData);
        this.getWorldManager(islandData.getIslandType()).islandAdded(islandData);
    }

    @Override
    public void islandRemoved(final UUID islandId)
    {
        this.logger.info("[SkyBlock] Received info about island to remove: " + islandId);
    }

    @Override
    public void islandDataChanged(final UUID islandId)
    {
        this.logger.info("[SkyBlock] Received info about new island data: " + islandId);
    }
}
