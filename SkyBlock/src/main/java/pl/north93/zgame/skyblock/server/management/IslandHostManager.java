package pl.north93.zgame.skyblock.server.management;

import static java.lang.System.currentTimeMillis;


import java.io.File;
import java.util.Collection;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectNewInstance;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Lock;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.skyblock.shared.api.IIslandHostManager;
import pl.north93.zgame.skyblock.shared.api.IslandDao;
import pl.north93.zgame.skyblock.shared.api.IslandData;
import pl.north93.zgame.skyblock.shared.api.NorthBiome;
import pl.north93.zgame.skyblock.shared.api.cfg.IslandConfig;
import pl.north93.zgame.skyblock.shared.api.player.SkyPlayer;
import pl.north93.zgame.skyblock.shared.api.utils.Coords2D;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyblock.server.actions.TeleportPlayerToIsland;
import pl.north93.zgame.skyblock.server.world.AutoMobNerf;
import pl.north93.zgame.skyblock.server.world.Island;
import pl.north93.zgame.skyblock.server.world.MobSpawningFix;
import pl.north93.zgame.skyblock.server.world.WorldManager;
import pl.north93.zgame.skyblock.server.world.WorldManagerList;
import pl.north93.zgame.skyblock.server.world.points.PointsHelper;

/**
 * Klasa zarządzająca serwerem pracującym w trybie hosta wysp.
 */
public class IslandHostManager implements ISkyBlockServerManager, IIslandHostManager
{
    private static final int POINTS_PERSIST_TICK = 5 * 60 * 20;
    private BukkitApiCore       bukkitApiCore;
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager     networkManager;
    @InjectComponent("SkyBlock.Server")
    private SkyBlockServer      skyBlockServer;
    @InjectComponent("API.Database.Redis.RPC")
    private IRpcManager         rpcManager;
    @InjectComponent("API.Database.Redis.Observer")
    private IObservationManager observer;
    private Logger              logger;
    @InjectNewInstance
    private WorldManagerList    worldManagers;
    @InjectNewInstance
    private PointsHelper        pointsHelper;

    @Override
    public void start()
    {
        this.pointsHelper.load(this.skyBlockServer.getSkyBlockConfig().getBlockValues());
        this.bukkitApiCore.getPlatformConnector().runTaskAsynchronously(() -> this.pointsHelper.persistAll(), POINTS_PERSIST_TICK);
        this.bukkitApiCore.getPlatformConnector().runTaskAsynchronously(new AutoMobNerf(), 100); // server lag reduction every 5 sec
        try
        {
            MobSpawningFix.applyChange(this.bukkitApiCore.getInstrumentationClient());
            this.logger.info("[SkyBlock] Applied mob-spawning fix.");
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
        final File schematics = this.bukkitApiCore.getFile("schematics");
        if (! schematics.exists())
        {
            final boolean mkdir = schematics.mkdir();
            this.logger.info("[SkyBlock] Created schematics folder... (" + mkdir + ")");
        }
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

        this.logger.info("[SkyBlock] World " + worldName + " is ready! Creating world manager...");

        final WorldManager manager = new WorldManager(islandConfig, world);
        manager.init();
        this.worldManagers.add(manager);
    }

    private void exportIslandsTo(final UUID newServerId)
    {
        this.logger.info("Starting islands export to " + newServerId);
        for (final WorldManager worldManager : this.getWorldManagers())
        {
            worldManager.getIslands().forEach(island ->
            {
                this.logger.info("Exporting island " + island.getId());
                final IslandDao islandDao = this.skyBlockServer.getIslandDao();
                final IslandData islandData = islandDao.getIsland(island.getId());
                islandData.setServerId(newServerId);
                islandDao.saveIsland(islandData);
            });
        }
    }

    @Override
    public void stop()
    {
        this.pointsHelper.persistAll();
    }

    @Override
    public Lock getIslandDataLock(final UUID islandId)
    {
        return this.observer.getLock("lock:isldata:" + islandId);
    }

    @Override
    public void tpPlayerToIsland(final Player player, final UUID islandId)
    {
        final IslandData islandData = this.skyBlockServer.getIslandDao().getIsland(islandId);
        if (this.bukkitApiCore.getServer().get().getUuid().equals(islandData.getServerId()))
        {
            player.teleport(this.getWorldManager(islandData.getIslandType()).getIslands().getById(islandId).getHomeLocation());
        }
        else
        {
            final Value<IOnlinePlayer> networkPlayer = this.networkManager.getOnlinePlayer(player.getName());
            networkPlayer.get().connectTo(this.networkManager.getServer(islandData.getServerId()).get(), new TeleportPlayerToIsland(islandId));
        }
    }

    @Override
    public void tpPlayerToSpawn(final Player player)
    {
        this.networkManager.getOnlinePlayer(player.getName()).get().connectTo(this.skyBlockServer.getSkyBlockConfig().getLobbyServersGroup());
    }

    @Override
    public boolean canGenerateIsland(final SkyPlayer skyPlayer)
    {
        final long islandCooldown = skyPlayer.getIslandCooldown();
        return islandCooldown == 0 || (currentTimeMillis() - islandCooldown) > this.skyBlockServer.getSkyBlockConfig().getIslandGenerateCooldown();
    }

    @Override
    public Island getIslandAt(final Location location)
    {
        final WorldManager manager = this.getWorldManager(location.getWorld());
        if (manager == null)
        {
            return null;
        }

        final Island island = manager.getIslands().getByChunk(location.getChunk());
        if (island == null || ! island.getLocation().isInside(location))
        {
            return null;
        }
        return island;
    }

    /**
     * Zwraca klasę pomocniczą obsługującą punkty i ranking wysp.
     * @return klasa do zarządzania punktami wysp.
     */
    public PointsHelper getPointsHelper()
    {
        return this.pointsHelper;
    }

    /**
     * Zwraca menadżera światów dla danego typu wyspy.
     *
     * @param islandType nazwa typu wyspy.
     * @return menadżer świata.
     */
    public WorldManager getWorldManager(final String islandType)
    {
        return this.worldManagers.get(islandType);
    }

    /**
     * Zwraca menadżera światów dla danego świata.
     *
     * @param world świat.
     * @return menadżer świata.
     */
    public WorldManager getWorldManager(final World world)
    {
        return this.worldManagers.get(world);
    }

    /**
     * Zwraca listę menadżerów światów na tym serwerze.
     * @return menadżery światów.
     */
    public Collection<WorldManager> getWorldManagers()
    {
        return this.worldManagers.list();
    }

    /**
     * Zwraca obiekt wyspy znajdującej się NA TYM SERWERZE.
     * Jeśli wyspa jest na innym serwerze, ta metoda zwróci null.
     *
     * @param islandId identyfikator wyspy.
     * @return obiekt wyspy na tym serwerze.
     */
    public Island getIsland(final UUID islandId)
    {
        final IslandData islandData = this.skyBlockServer.getIslandDao().getIsland(islandId);
        if (islandData == null)
        {
            return null;
        }
        return this.getWorldManager(islandData.getIslandType()).getIslands().getById(islandId);
    }

    /**
     * @return zwraca ilość wysp na tym serwerze.
     */
    @Override
    public Integer getIslands()
    {
        return this.worldManagers.list().stream().mapToInt(WorldManager::getIslandsCount).sum();
    }

    @Override
    public Coords2D getFirstFreeLocation(final String islandType)
    {
        return this.getWorldManager(islandType).getFirstFreeLocation();
    }

    @Override
    public void recalculatePoints()
    {
        this.pointsHelper.recalculateAll();
    }

    @Override
    public void tpToIsland(final UUID playerId, final IslandData islandData)
    {
        final Player player = Bukkit.getPlayer(playerId);
        if (player == null)
        {
            return;
        }

        final Island island = this.getWorldManager(islandData.getIslandType()).getIslands().getById(islandData.getIslandId());
        if (island == null)
        {
            return;
        }

        this.bukkitApiCore.sync(() -> player.teleport(island.getHomeLocation()));
    }

    @Override
    public void islandAdded(UUID islandId, String islandType)
    {
        this.bukkitApiCore.debug("[SkyBlock] Received info about new island: " + islandId);
        this.getWorldManager(islandType).islandAdded(islandId);
    }

    @Override
    public void islandRemoved(final IslandData islandData)
    {
        this.bukkitApiCore.debug("[SkyBlock] Received info about island to remove: " + islandData);
        this.getWorldManager(islandData.getIslandType()).islandRemoved(islandData);
    }

    @Override
    public void biomeChanged(final UUID islandId, final String islandType, final NorthBiome newBiome)
    {
        final Island island = this.getWorldManager(islandType).getIslands().getById(islandId);
        this.bukkitApiCore.sync(() -> island.setBiome(newBiome));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("worldManagers", this.worldManagers).toString();
    }
}
