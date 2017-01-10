package pl.north93.zgame.skyblock.server.management;

import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.skyblock.api.IIslandHostManager;
import pl.north93.zgame.skyblock.api.IslandData;
import pl.north93.zgame.skyblock.server.actions.TeleportPlayerToIsland;
import pl.north93.zgame.skyblock.api.cfg.IslandConfig;
import pl.north93.zgame.skyblock.api.utils.Coords2D;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyblock.server.world.Island;
import pl.north93.zgame.skyblock.server.world.WorldManager;
import pl.north93.zgame.skyblock.server.world.WorldManagerList;

/**
 * Klasa zarządzająca serwerem pracującym w trybie hosta wysp.
 */
public class IslandHostManager implements ISkyBlockServerManager, IIslandHostManager
{
    private BukkitApiCore      bukkitApiCore;
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager    networkManager;
    @InjectComponent("SkyBlock.Server")
    private SkyBlockServer     skyBlockServer;
    @InjectComponent("API.Database.Redis.RPC")
    private IRpcManager        rpcManager;
    private Logger             logger;
    private WorldManagerList   worldManagers;

    @Override
    public void start()
    {
        this.worldManagers = new WorldManagerList();
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
    public void islandAdded(final IslandData islandData)
    {
        this.bukkitApiCore.debug("[SkyBlock] Received info about new island: " + islandData);
        this.getWorldManager(islandData.getIslandType()).islandAdded(islandData);
    }

    @Override
    public void islandRemoved(final IslandData islandData)
    {
        this.bukkitApiCore.debug("[SkyBlock] Received info about island to remove: " + islandData);
        this.getWorldManager(islandData.getIslandType()).islandRemoved(islandData);
    }

    @Override
    public void islandDataChanged(final IslandData islandData)
    {
        this.bukkitApiCore.debug("[SkyBlock] Received info about new island data: " + islandData);
        this.getWorldManager(islandData.getIslandType()).islandUpdated(islandData);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("worldManagers", this.worldManagers).toString();
    }
}
