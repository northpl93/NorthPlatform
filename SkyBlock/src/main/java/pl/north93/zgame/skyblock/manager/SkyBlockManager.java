package pl.north93.zgame.skyblock.manager;

import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.stream.Collectors;

import pl.north93.zgame.api.global.cfg.ConfigUtils;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectResource;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.IOfflinePlayer;
import pl.north93.zgame.api.global.network.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.skyblock.api.ISkyBlockManager;
import pl.north93.zgame.skyblock.api.IslandDao;
import pl.north93.zgame.skyblock.api.IslandData;
import pl.north93.zgame.skyblock.api.ServerMode;
import pl.north93.zgame.skyblock.api.cfg.IslandConfig;
import pl.north93.zgame.skyblock.api.cfg.SkyBlockConfig;
import pl.north93.zgame.skyblock.api.player.SkyPlayer;
import pl.north93.zgame.skyblock.api.utils.Coords2D;
import pl.north93.zgame.skyblock.manager.servers.IslandHostManagers;
import pl.north93.zgame.skyblock.manager.servers.IslandHostServer;

public class SkyBlockManager extends Component implements ISkyBlockManager
{
    @InjectComponent("API.Database.Redis.RPC")
    private IRpcManager        rpcManager;
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager    networkManager;
    @InjectResource(bundleName = "SkyBlock")
    private ResourceBundle     messages;
    private IslandDao          islandDao;
    private IslandHostManagers islandHostManager;
    private SkyBlockConfig     skyBlockConfig;

    @Override
    protected void enableComponent()
    {
        if (!this.getApiCore().getId().equals("controller"))
        {
            return; // for developer env
        }
        this.islandHostManager = new IslandHostManagers();
        this.islandDao = new IslandDao();
        this.skyBlockConfig = ConfigUtils.loadConfigFile(SkyBlockConfig.class, this.getApiCore().getFile("skyblock.yml"));
        this.rpcManager.addRpcImplementation(ISkyBlockManager.class, this);
    }

    @Override
    protected void disableComponent()
    {
    }

    @Override
    public ServerMode serverJoin(final UUID serverId)
    {
        final List<UUID> servers = this.skyBlockConfig.getSkyBlockServers().stream().map(UUID::fromString).collect(Collectors.toList());;
        if (servers.contains(serverId))
        {
            this.islandHostManager.serverConnect(serverId);
            return ServerMode.ISLAND_HOST;
        }
        return ServerMode.LOBBY;
    }

    @Override
    public void serverDisconnect(final UUID serverId)
    {
        this.islandHostManager.serverDisconnect(serverId);
    }

    @Override
    public SkyBlockConfig getConfig()
    {
        return this.skyBlockConfig;
    }

    @Override
    public void teleportToIsland(final String playerName, final UUID islandId)
    {
        final Value<IOnlinePlayer> onlinePlayer = this.networkManager.getOnlinePlayer(playerName);
        if (! onlinePlayer.isAvailable())
        {
            return;
        }

        final IslandData islandData = this.islandDao.getIsland(islandId);
        final UUID islandServer = islandData.getServerId();
        if (onlinePlayer.get().getServerId().equals(islandServer))
        {
            this.islandHostManager.getServer(islandServer).getIslandHostManager().tpToIsland(onlinePlayer.get().getUuid(), islandData);
        }
        else
        {
            final SkyPlayer skyPlayer = SkyPlayer.get(onlinePlayer);
            skyPlayer.setIslandToTp(islandId);
            onlinePlayer.get().connectTo(this.networkManager.getServer(islandServer).get());
        }
    }

    @Override
    public void updateIslandData(final IslandData islandData)
    {

    }

    @Override
    public void createIsland(final String islandType, final String ownerNick)
    {
        this.getLogger().info("Owner " + ownerNick + " requested island of type " + islandType);
        final IslandConfig config = this.getConfig().getIslandType(islandType);
        if (config == null)
        {
            return;
        }

        final IslandHostServer server = this.islandHostManager.getLeastLoadedServer();
        if (server == null)
        {
            return;
        }
        this.getLogger().info("Found server for this island: " + server.getUuid());

        final Value<IOnlinePlayer> networkPlayer = this.networkManager.getOnlinePlayer(ownerNick);
        final SkyPlayer skyPlayer = SkyPlayer.get(networkPlayer);

        final UUID islandId = UUID.randomUUID();
        this.getLogger().info("Island ID: " + islandId);

        final Coords2D islandLocation = server.getIslandHostManager().getFirstFreeLocation(islandType);
        this.getLogger().info("Island location: " + islandLocation);

        final IslandData island = new IslandData();
        island.setIslandId(islandId);
        island.setOwnerId(networkPlayer.get().getUuid());
        island.setServerId(server.getUuid());
        island.setIslandLocation(islandLocation);
        island.setIslandType(islandType);
        island.setName("Wyspa gracza " + ownerNick);
        island.setHomeLocation(config.getHomeLocation());

        this.islandDao.saveIsland(island);
        skyPlayer.setIsland(islandId);
        skyPlayer.setIslandToTp(islandId);

        server.getIslandHostManager().islandAdded(island);

        networkPlayer.get().sendMessage(this.messages, "info.created_island");
        networkPlayer.get().connectTo(server.getServerValue().get());
    }

    @Override
    public void deleteIsland(final UUID islandId)
    {
        this.getLogger().info("Island " + islandId + " will be removed!");
        final IslandData data = this.islandDao.getIsland(islandId);
        if (data == null)
        {
            this.getLogger().warning("islandId is null in SkyBlockManager#deleteIsland(" + islandId + ")");
            return;
        }
        this.islandDao.deleteIsland(islandId);

        final IslandHostServer server = this.islandHostManager.getServer(data.getServerId());
        server.getIslandHostManager().islandRemoved(data);

        final Value<IOnlinePlayer> onlinePlayerValue = this.networkManager.getOnlinePlayer(data.getOwnerId());
        if (onlinePlayerValue.isAvailable()) // is online
        {
            final SkyPlayer skyPlayer = SkyPlayer.get(onlinePlayerValue);
            skyPlayer.setIsland(null);
        }
        else
        {
            final IOfflinePlayer offlinePlayer = this.networkManager.getOfflinePlayer(data.getOwnerId());
            final SkyPlayer skyPlayer = SkyPlayer.get(offlinePlayer);
            skyPlayer.setIsland(null);
            this.networkManager.savePlayer(offlinePlayer);
        }
    }
}
