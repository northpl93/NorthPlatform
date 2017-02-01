package pl.north93.zgame.skyblock.manager;

import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.cfg.ConfigUtils;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.IncludeInScanning;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectResource;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOfflinePlayer;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.skyblock.api.NorthBiome;
import pl.north93.zgame.skyblock.api.ISkyBlockManager;
import pl.north93.zgame.skyblock.api.IslandDao;
import pl.north93.zgame.skyblock.api.IslandData;
import pl.north93.zgame.skyblock.api.IslandRole;
import pl.north93.zgame.skyblock.api.ServerMode;
import pl.north93.zgame.skyblock.api.cfg.IslandConfig;
import pl.north93.zgame.skyblock.api.cfg.SkyBlockConfig;
import pl.north93.zgame.skyblock.api.player.SkyPlayer;
import pl.north93.zgame.skyblock.api.utils.Coords2D;
import pl.north93.zgame.skyblock.manager.servers.IslandHostManagers;
import pl.north93.zgame.skyblock.manager.servers.IslandHostServer;
import pl.north93.zgame.skyblock.server.actions.TeleportPlayerToIsland;

@IncludeInScanning("pl.north93.zgame.skyblock.api")
public class SkyBlockManager extends Component implements ISkyBlockManager
{
    @InjectComponent("API.Database.Redis.RPC")
    private IRpcManager         rpcManager;
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager     networkManager;
    @InjectComponent("API.Database.Redis.Observer")
    private IObservationManager observer;
    @InjectResource(bundleName = "SkyBlock")
    private ResourceBundle      messages;
    private IslandDao           islandDao;
    private IslandHostManagers  islandHostManager;
    private SkyBlockConfig      skyBlockConfig;

    @Override
    protected void enableComponent()
    {
        if (! this.getApiCore().getId().equals("controller"))
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
        final List<UUID> servers = this.skyBlockConfig.getSkyBlockServers().stream().map(UUID::fromString).collect(Collectors.toList());
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
        final SkyPlayer skyPlayer = SkyPlayer.get(onlinePlayer);

        final IslandData islandData = this.islandDao.getIsland(islandId);
        if (islandData == null)
        {
            this.getLogger().severe("IslandData is null in teleportToIsland(" + playerName + ", " + islandId + ")");
            skyPlayer.setIsland(null, null);
            return;
        }

        final UUID islandServer = islandData.getServerId();
        if (onlinePlayer.get().getServerId().equals(islandServer))
        {
            this.islandHostManager.getServer(islandServer).getIslandHostManager().tpToIsland(onlinePlayer.get().getUuid(), islandData);
        }
        else
        {
            onlinePlayer.get().connectTo(this.networkManager.getServer(islandServer).get(), new TeleportPlayerToIsland(islandId)); // todo server may be null?
        }
    }

    @Override
    public void createIsland(final String islandType, final String ownerNick)
    {
        this.getLogger().info("Owner " + ownerNick + " requested island of type " + islandType);
        final IslandConfig config = this.getConfig().getIslandType(islandType);
        if (config == null)
        {
            this.getLogger().warning("Not found island type: " + islandType);
            return;
        }

        final IslandHostServer server = this.islandHostManager.getLeastLoadedServer();
        if (server == null)
        {
            this.getLogger().warning("I can't find best server for island");
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
        island.setAcceptingVisits(false);
        island.setName("Wyspa gracza " + ownerNick);
        island.setBiome(NorthBiome.OVERWORLD);
        island.setHomeLocation(config.getHomeLocation());

        skyPlayer.setIsland(islandId, IslandRole.OWNER);

        this.islandDao.saveIsland(island);
        server.getIslandHostManager().islandAdded(islandId, islandType);

        networkPlayer.get().sendMessage(this.messages, "info.created_island");
        networkPlayer.get().connectTo(server.getServerValue().get(), new TeleportPlayerToIsland(islandId));
    }

    @Override
    public void deleteIsland(final UUID islandId)
    {
        this.getLogger().info("Island " + islandId + " will be removed!");
        final IslandData data = this.islandDao.getIsland(islandId);
        if (data == null)
        {
            this.getLogger().warning("data is null in SkyBlockManager#deleteIsland(" + islandId + ")");
            return;
        }
        this.islandDao.deleteIsland(islandId);

        this.deleteIslandFromPlayer(data.getOwnerId());
        data.getMembersUuid().forEach(this::deleteIslandFromPlayer);

        final IslandHostServer server = this.islandHostManager.getServer(data.getServerId());
        server.getIslandHostManager().islandRemoved(data); // we must send data because it's already removed from database
    }

    @Override
    public void changeBiome(final UUID islandId, final NorthBiome biome)
    {
        this.islandDao.modifyIsland(islandId, data ->
        {
            data.setBiome(biome);
            final IslandHostServer server = this.islandHostManager.getServer(data.getServerId());
            server.getIslandHostManager().biomeChanged(islandId, data.getIslandType(), biome);
        });
    }

    private void deleteIslandFromPlayer(final UUID playerId)
    {
        final Value<IOnlinePlayer> onlinePlayerValue = this.networkManager.getOnlinePlayer(playerId);
        if (onlinePlayerValue.isAvailable()) // is online
        {
            final SkyPlayer skyPlayer = SkyPlayer.get(onlinePlayerValue);
            skyPlayer.setIsland(null, null);
        }
        else
        {
            final IOfflinePlayer offlinePlayer = this.networkManager.getOfflinePlayer(playerId);
            final SkyPlayer skyPlayer = SkyPlayer.get(offlinePlayer);
            skyPlayer.setIsland(null, null);
            this.networkManager.savePlayer(offlinePlayer);
        }
    }

    @Override
    public void invitePlayer(final UUID islandId, final String invitedPlayer)
    {
        this.islandDao.modifyIsland(islandId, islandData ->
        {
            final Value<IOnlinePlayer> islandOwner = this.networkManager.getOnlinePlayer(islandData.getOwnerId());

            final Value<IOnlinePlayer> invited = this.networkManager.getOnlinePlayer(invitedPlayer);
            if (! invited.isAvailable())
            {
                islandOwner.ifPresent(player -> player.sendMessage(this.messages, "error.invite.no_player"));
                return;
            }
            if (SkyPlayer.get(invited).hasIsland())
            {
                islandOwner.ifPresent(player -> player.sendMessage(this.messages, "error.invite.already_has_island"));
                return;
            }
            final IOnlinePlayer invitedOnline = invited.get();
            final UUID invitedId = invitedOnline.getUuid();

            if (islandData.getInvitations().contains(invitedId))
            {
                islandOwner.ifPresent(player -> player.sendMessage(this.messages, "error.invite.already_invited"));
            }
            else
            {
                islandData.getInvitations().add(invitedId);

                invitedOnline.sendMessage(this.messages, "info.invited", this.networkManager.getNickFromUuid(islandData.getOwnerId()));

                islandOwner.ifPresent(player -> player.sendMessage(this.messages, "info.successfully_invited", invitedOnline.getNick()));
            }
        });
    }

    @Override
    public void invitationAccepted(final UUID islandId, final String invitedPlayer)
    {
        this.islandDao.modifyIsland(islandId, islandData ->
        {
            final Value<IOnlinePlayer> invited = this.networkManager.getOnlinePlayer(invitedPlayer);
            if (! invited.isAvailable())
            {
                return;
            }
            final IOnlinePlayer onlinePlayer = invited.get();
            final SkyPlayer invitedSky = SkyPlayer.get(invited);
            if (invitedSky.hasIsland())
            {
                onlinePlayer.sendMessage(this.messages, "error.you_must_have_not_island");
                return;
            }
            if (! islandData.getInvitations().contains(onlinePlayer.getUuid()))
            {
                onlinePlayer.sendMessage(this.messages, "cmd.accept.no_invite");
                return;
            }
            invitedSky.setIsland(islandId, IslandRole.MEMBER);
            islandData.getInvitations().remove(onlinePlayer.getUuid());
            islandData.addMember(onlinePlayer.getUuid());
            onlinePlayer.sendMessage(this.messages, "info.invitation_accepted");
        });
    }

    @Override
    public void leaveIsland(final UUID islandId, final String invoker, final String leavingPlayer, final Boolean isSelfLeaving)
    {
        final Value<IOnlinePlayer> player = this.networkManager.getOnlinePlayer(invoker);
        this.islandDao.modifyIsland(islandId, islandData ->
        {
            final UUID playerId = this.networkManager.getUuidFromNick(leavingPlayer);
            if (playerId == null)
            {
                player.ifPresent(p -> p.sendMessage(this.messages, "cmd.invites.no_player"));
                return;
            }
            if (! islandData.getMembersUuid().contains(playerId))
            {
                player.ifPresent(p -> p.sendMessage(this.messages, "cmd.invites.no_player"));
                return;
            }
            this.deleteIslandFromPlayer(playerId);
            islandData.removeMember(playerId);

            this.networkManager.getPlayers().ifOnline(leavingPlayer, p -> p.sendMessage(this.messages, "info.removed_from_members.removed"));
            if (! isSelfLeaving)
            {
                player.ifPresent(p -> p.sendMessage(this.messages, "info.removed_from_members.remover"));
            }
        });
    }

    @Override
    public void visitIsland(final UUID islandId, final String visitor)
    {
        final Value<IOnlinePlayer> visitorValue = this.networkManager.getOnlinePlayer(visitor);
        if (! visitorValue.isAvailable())
        {
            return;
        }

        final IslandData islandData = this.islandDao.getIsland(islandId);
        if (islandData == null)
        {
            this.getLogger().severe("IslandData is null in visitIsland(" + islandId + ", " + visitor + ")");
            return;
        }

        if (! islandData.getAcceptingVisits())
        {
            visitorValue.get().sendMessage(this.messages, "cmd.visit.unavailable");
            return;
        }

        final UUID islandServer = islandData.getServerId();
        if (visitorValue.get().getServerId().equals(islandServer))
        {
            this.islandHostManager.getServer(islandServer).getIslandHostManager().tpToIsland(visitorValue.get().getUuid(), islandData);
        }
        else
        {
            visitorValue.get().connectTo(this.networkManager.getServer(islandServer).get(), new TeleportPlayerToIsland(islandId)); // todo server may be null?
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("islandDao", this.islandDao).append("skyBlockConfig", this.skyBlockConfig).toString();
    }
}
