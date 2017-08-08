package pl.north93.zgame.api.global.network.impl;

import static pl.north93.zgame.api.global.redis.RedisKeys.DAEMON;
import static pl.north93.zgame.api.global.redis.RedisKeys.NETWORK_ACTION;
import static pl.north93.zgame.api.global.redis.RedisKeys.PROXY_INSTANCE;
import static pl.north93.zgame.api.global.utils.StringUtils.asString;
import static pl.north93.zgame.api.global.utils.StringUtils.toBytes;


import java.util.Set;
import java.util.stream.Collectors;

import com.lambdaworks.redis.api.sync.RedisCommands;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.data.StorageConnector;
import pl.north93.zgame.api.global.data.players.IPlayersData;
import pl.north93.zgame.api.global.deployment.RemoteDaemon;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.JoiningPolicy;
import pl.north93.zgame.api.global.network.NetworkAction;
import pl.north93.zgame.api.global.network.NetworkControllerRpc;
import pl.north93.zgame.api.global.network.NetworkMeta;
import pl.north93.zgame.api.global.network.players.IPlayersManager;
import pl.north93.zgame.api.global.network.proxy.ProxyInstanceInfo;
import pl.north93.zgame.api.global.network.server.IServersManager;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.api.global.redis.rpc.Targets;
import pl.north93.zgame.api.global.redis.subscriber.RedisSubscriber;

class NetworkManager extends Component implements INetworkManager
{
    @Inject
    private TemplateManager     msgPack;
    @Inject
    private RedisSubscriber     redisSubscriber;
    @Inject
    private IObservationManager observationManager;
    @Inject
    private IRpcManager         rpcManager;
    @Inject
    private IPlayersData        playersData;
    @Inject
    private StorageConnector    storage;
    private ServersManagerImpl  serversManager;
    private PlayersManagerImpl  playersManager;
    private Value<NetworkMeta>  networkMeta;

    @Override
    protected void enableComponent()
    {
        this.networkMeta = this.observationManager.get(NetworkMeta.class, "configs_networkMeta"); // todo zamienic to na odwolanie do systemu configow
        this.redisSubscriber.subscribe(NETWORK_ACTION, this::handleNetworkAction);
        this.serversManager = new ServersManagerImpl(this.storage, this, this.msgPack, this.observationManager);
        this.playersManager = new PlayersManagerImpl(this, this.playersData, this.observationManager);
    }

    @Override
    protected void disableComponent()
    {
    }

    @Override
    public Value<NetworkMeta> getNetworkMeta()
    {
        return this.networkMeta;
    }

    @Override
    public JoiningPolicy getJoiningPolicy()
    {
        return this.networkMeta.get().joiningPolicy;
    }

    @Override
    public Set<ProxyInstanceInfo> getProxyServers()
    {
        final RedisCommands<String, byte[]> redis = this.storage.getRedis();
        return redis.keys(PROXY_INSTANCE + "*").stream().map(id -> this.msgPack.deserialize(ProxyInstanceInfo.class, redis.get(id))).collect(Collectors.toSet());
    }

    @Override
    public Set<RemoteDaemon> getDaemons()
    {
        final RedisCommands<String, byte[]> redis = this.storage.getRedis();
        return redis.keys(DAEMON + "*").stream().map(id -> this.msgPack.deserialize(RemoteDaemon.class, redis.get(id))).collect(Collectors.toSet());
    }

    /**
     * Wysyła wiadomość sieciową do wszystkich komponentów,
     * łącznie z tą instancją.
     *
     * @param networkAction akcja do wykonania
     */
    @Override
    public void broadcastNetworkAction(final NetworkAction networkAction)
    {
        this.storage.getRedis().publish(NETWORK_ACTION, toBytes(networkAction.toString()));
    }

    /**
     * Zwraca instancję zdalnego wywoływania procedur do kontrolera sieci.
     *
     * @return NetworkControllerRpc
     */
    @Override
    public NetworkControllerRpc getNetworkController()
    {
        return this.rpcManager.createRpcProxy(NetworkControllerRpc.class, Targets.networkController());
    }

    @Override
    public IPlayersManager getPlayers()
    {
        return this.playersManager;
    }

    @Override
    public IServersManager getServers()
    {
        return this.serversManager;
    }

    private void handleNetworkAction(final String channel, final byte[] message)
    {
        final NetworkAction networkAction = NetworkAction.valueOf(asString(message));
        switch (networkAction)
        {
            case STOP_ALL:
                API.getPlatformConnector().stop();
                break;
            case KICK_ALL:
                API.getPlatformConnector().kickAll();
                break;
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
