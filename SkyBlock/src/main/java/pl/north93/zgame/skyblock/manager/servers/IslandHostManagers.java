package pl.north93.zgame.skyblock.manager.servers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.tuple.MutablePair;

import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.api.global.redis.rpc.Targets;
import pl.north93.zgame.skyblock.api.IIslandHostManager;

public class IslandHostManagers
{
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager networkManager;
    @InjectComponent("API.Database.Redis.RPC")
    private IRpcManager     rpcManager;
    private Logger          logger;
    private final List<IslandHostServer> servers;

    public IslandHostManagers()
    {
        this.servers = new ArrayList<>();
    }

    public void serverConnect(final UUID serverId)
    {
        final IIslandHostManager rpcProxy = this.rpcManager.createRpcProxy(IIslandHostManager.class, Targets.server(serverId));
        final Value<Server> server = this.networkManager.getServer(serverId);

        final IslandHostServer islandHost = new IslandHostServer(serverId, rpcProxy, server);
        this.servers.add(islandHost);

        this.logger.info("[SkyBlock] Server with ID " + serverId + " connected to network.");
    }

    public void serverDisconnect(final UUID serverId)
    {
        if (this.servers.removeIf(host -> host.getUuid().equals(serverId)))
        {
            this.logger.info("[SkyBlock] Server with ID " + serverId + " disconnected from network.");
        }
    }

    /**
     * Zwraca najmniej obciążony serwer.
     *
     * @return Serwer z najmniejszą ilością wysp.
     */
    public IslandHostServer getLeastLoadedServer()
    {
        final MutablePair<IslandHostServer, Integer> leastLoaded = new MutablePair<>();
        for (final IslandHostServer server : this.servers)
        {
            final Integer serverIslands = server.getIslandHostManager().getIslands();
            if (leastLoaded.getLeft() == null)
            {
                leastLoaded.setLeft(server);
                leastLoaded.setRight(serverIslands);
            }
            else
            {
                if (leastLoaded.getRight() > serverIslands)
                {
                    leastLoaded.setLeft(server);
                    leastLoaded.setRight(serverIslands);
                }
            }
        }

        return leastLoaded.getLeft();
    }

    public IslandHostServer getServer(final UUID serverId)
    {
        for (final IslandHostServer server : this.servers)
        {
            if (server.getUuid().equals(serverId))
            {
                return server;
            }
        }
        return null;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("servers", this.servers).toString();
    }
}
