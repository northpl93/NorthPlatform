package pl.north93.zgame.skyblock.manager;

import java.util.List;
import java.util.UUID;

import pl.north93.zgame.api.global.cfg.ConfigUtils;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.skyblock.api.ISkyBlockManager;
import pl.north93.zgame.skyblock.api.IslandDao;
import pl.north93.zgame.skyblock.api.IslandData;
import pl.north93.zgame.skyblock.api.ServerMode;
import pl.north93.zgame.skyblock.api.SkyPlayer;
import pl.north93.zgame.skyblock.api.cfg.IslandConfig;
import pl.north93.zgame.skyblock.api.cfg.SkyBlockConfig;
import pl.north93.zgame.skyblock.api.utils.Coords2D;
import pl.north93.zgame.skyblock.manager.servers.IslandHostManagers;
import pl.north93.zgame.skyblock.manager.servers.IslandHostServer;

public class SkyBlockManager extends Component implements ISkyBlockManager
{
    @InjectComponent("API.Database.Redis.RPC")
    private IRpcManager        rpcManager;
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager    networkManager;
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
        final List<UUID> servers = this.skyBlockConfig.getSkyBlockServers();
        servers.add(serverId); // TODO needed in debug mode. REMOVE IT.
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
    public Boolean createIsland(final String islandType, final String ownerNick)
    {
        this.getLogger().info("Owner " + ownerNick + " requested island of type " + islandType);
        final IslandConfig config = this.getConfig().getIslandType(islandType);
        if (config == null)
        {
            return false;
        }

        final IslandHostServer server = this.islandHostManager.getLeastLoadedServer();
        if (server == null)
        {
            return false;
        }
        this.getLogger().info("Found server for this island: " + server.getUuid());

        final SkyPlayer skyPlayer = new SkyPlayer(this.networkManager.getNetworkPlayer(ownerNick));

        final UUID islandId = UUID.randomUUID();
        this.getLogger().info("Island ID: " + islandId);

        final Coords2D islandLocation = server.getIslandHostManager().getFirstFreeLocation(islandType);
        this.getLogger().info("Island location: " + islandLocation);

        final IslandData island = new IslandData();
        island.setIslandId(islandId);
        island.setOwnerId(skyPlayer.getNetworkPlayer().get().getUuid());
        island.setServerId(server.getUuid());
        island.setIslandLocation(islandLocation);
        island.setIslandType(islandType);
        island.setName("Wyspa gracza " + ownerNick);
        island.setHomeLocation(config.getHomeLocation());

        this.islandDao.saveIsland(island);
        skyPlayer.setIsland(islandId);

        server.getIslandHostManager().islandAdded(island);

        return true;
    }

    @Override
    public void deleteIsland(final UUID islandId)
    {
        // todo
    }
}
