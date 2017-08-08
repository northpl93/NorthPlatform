package pl.north93.zgame.skyblock.manager;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.cfg.ConfigUtils;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.IncludeInScanning;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.skyblock.manager.servers.IslandHostManagers;
import pl.north93.zgame.skyblock.manager.servers.IslandHostServer;
import pl.north93.zgame.skyblock.server.actions.TeleportPlayerToIsland;
import pl.north93.zgame.skyblock.shared.api.IIslandsRanking;
import pl.north93.zgame.skyblock.shared.api.ISkyBlockManager;
import pl.north93.zgame.skyblock.shared.api.IslandData;
import pl.north93.zgame.skyblock.shared.api.IslandRole;
import pl.north93.zgame.skyblock.shared.api.NorthBiome;
import pl.north93.zgame.skyblock.shared.api.ServerMode;
import pl.north93.zgame.skyblock.shared.api.cfg.IslandConfig;
import pl.north93.zgame.skyblock.shared.api.cfg.SkyBlockConfig;
import pl.north93.zgame.skyblock.shared.api.player.SkyPlayer;
import pl.north93.zgame.skyblock.shared.api.utils.Coords2D;
import pl.north93.zgame.skyblock.shared.impl.IslandDao;
import pl.north93.zgame.skyblock.shared.impl.IslandsRankingImpl;

@IncludeInScanning("pl.north93.zgame.skyblock.shared")
public class SkyBlockManager extends Component implements ISkyBlockManager
{
    @Inject
    private IRpcManager         rpcManager;
    @Inject
    private INetworkManager     networkManager;
    @Inject
    private IObservationManager observer;
    @Inject @Messages("SkyBlock")
    private MessagesBox         messages;
    private IslandDao           islandDao;
    private IslandHostManagers  islandHostManager;
    private IIslandsRanking     IIslandsRanking;
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
        this.IIslandsRanking = new IslandsRankingImpl();
        this.islandDao.moveDataToRedisRanking(this.IIslandsRanking);
        this.skyBlockConfig = ConfigUtils.loadConfigFile(SkyBlockConfig.class, this.getApiCore().getFile("skyblock.yml"));
        this.rpcManager.addRpcImplementation(ISkyBlockManager.class, this);
    }

    @Override
    protected void disableComponent()
    {
        this.IIslandsRanking.clearRanking();
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
        final Value<IOnlinePlayer> onlinePlayer = this.networkManager.getPlayers().unsafe().getOnline(playerName);
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
            onlinePlayer.get().connectTo(this.networkManager.getServers().withUuid(islandServer), new TeleportPlayerToIsland(islandId)); // todo server may be null?
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

        final Value<IOnlinePlayer> networkPlayer = this.networkManager.getPlayers().unsafe().getOnline(ownerNick);
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
        island.setPoints(0D);
        island.setShowInRanking(true);
        island.setHomeLocation(config.getHomeLocation());

        skyPlayer.setIsland(islandId, IslandRole.OWNER);

        this.islandDao.saveIsland(island);
        server.getIslandHostManager().islandAdded(islandId, islandType);
        this.IIslandsRanking.setPoints(islandId, 0); // add island to ranking

        networkPlayer.get().sendMessage(this.messages, "info.created_island");
        networkPlayer.get().connectTo(server.getServer(), new TeleportPlayerToIsland(islandId));
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

        this.IIslandsRanking.removeIsland(islandId); // remove island from ranking
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
        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(playerId))
        {
            final SkyPlayer skyPlayer = SkyPlayer.get(t.getPlayer());
            skyPlayer.setIsland(null, null);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void invitePlayer(final UUID islandId, final String invitedPlayer)
    {
        this.islandDao.modifyIsland(islandId, islandData ->
        {
            final Value<IOnlinePlayer> islandOwner = this.networkManager.getPlayers().unsafe().getOnline(islandData.getOwnerId());

            final Value<IOnlinePlayer> invited = this.networkManager.getPlayers().unsafe().getOnline(invitedPlayer);
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

                invitedOnline.sendMessage(this.messages, "info.invited", this.networkManager.getPlayers().getNickFromUuid(islandData.getOwnerId()));

                islandOwner.ifPresent(player -> player.sendMessage(this.messages, "info.successfully_invited", invitedOnline.getNick()));
            }
        });
    }

    @Override
    public void invitationAccepted(final UUID islandId, final String invitedPlayer)
    {
        this.islandDao.modifyIsland(islandId, islandData ->
        {
            final Value<IOnlinePlayer> invited = this.networkManager.getPlayers().unsafe().getOnline(invitedPlayer);
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
        final Value<IOnlinePlayer> player = this.networkManager.getPlayers().unsafe().getOnline(invoker);
        this.islandDao.modifyIsland(islandId, islandData ->
        {
            final UUID playerId = this.networkManager.getPlayers().getUuidFromNick(leavingPlayer);
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
        final Value<IOnlinePlayer> visitorValue = this.networkManager.getPlayers().unsafe().getOnline(visitor);
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

        final IOnlinePlayer visitorCache = visitorValue.get();
        if (! islandData.getAcceptingVisits() && ! visitorCache.getGroup().hasPermission("skyblock.visit.anyplayer"))
        {
            visitorCache.sendMessage(this.messages, "cmd.visit.unavailable");
            return;
        }

        final UUID islandServer = islandData.getServerId();
        if (visitorCache.getServerId().equals(islandServer))
        {
            this.islandHostManager.getServer(islandServer).getIslandHostManager().tpToIsland(visitorCache.getUuid(), islandData);
        }
        else
        {
            visitorCache.connectTo(this.networkManager.getServers().withUuid(islandServer), new TeleportPlayerToIsland(islandId)); // todo server may be null?
        }
    }

    @Override
    public void setShowInRanking(final UUID islandId, final Boolean newValue)
    {
        this.islandDao.modifyIsland(islandId, data ->
        {
            final boolean oldValue = data.getShowInRanking();
            if (oldValue && !newValue)
            {
                data.setShowInRanking(false);
                this.IIslandsRanking.removeIsland(islandId);
            }
            else if (!oldValue && newValue)
            {
                data.setShowInRanking(true);
                this.IIslandsRanking.setPoints(islandId, data.getPoints());
            }
        });
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("islandDao", this.islandDao).append("skyBlockConfig", this.skyBlockConfig).toString();
    }
}