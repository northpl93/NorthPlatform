package pl.north93.zgame.api.global.network.impl;

import static pl.north93.zgame.api.global.redis.RedisKeys.DAEMON;
import static pl.north93.zgame.api.global.redis.RedisKeys.NETWORK_ACTION;
import static pl.north93.zgame.api.global.redis.RedisKeys.NETWORK_MINIGAMES;
import static pl.north93.zgame.api.global.redis.RedisKeys.NETWORK_PATTERNS;
import static pl.north93.zgame.api.global.redis.RedisKeys.NETWORK_SERVER_GROUPS;
import static pl.north93.zgame.api.global.redis.RedisKeys.PLAYERS;
import static pl.north93.zgame.api.global.redis.RedisKeys.PROXY_INSTANCE;
import static pl.north93.zgame.api.global.redis.RedisKeys.SERVER;


import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.data.StorageConnector;
import pl.north93.zgame.api.global.data.players.IPlayersData;
import pl.north93.zgame.api.global.deployment.RemoteDaemon;
import pl.north93.zgame.api.global.deployment.ServerPattern;
import pl.north93.zgame.api.global.deployment.serversgroup.IServersGroup;
import pl.north93.zgame.api.global.messages.NetworkMeta;
import pl.north93.zgame.api.global.messages.ProxyInstanceInfo;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.IOfflinePlayer;
import pl.north93.zgame.api.global.network.IOnlinePlayer;
import pl.north93.zgame.api.global.network.IPlayer;
import pl.north93.zgame.api.global.network.JoiningPolicy;
import pl.north93.zgame.api.global.network.NetworkAction;
import pl.north93.zgame.api.global.network.NetworkControllerRpc;
import pl.north93.zgame.api.global.network.minigame.MiniGame;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.network.server.ServerImpl;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.api.global.redis.rpc.Targets;
import pl.north93.zgame.api.global.redis.subscriber.RedisSubscriber;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.util.SafeEncoder;

class NetworkManager extends Component implements INetworkManager
{
    @InjectComponent("API.Database.Redis.MessagePackSerializer")
    private TemplateManager     msgPack;
    @InjectComponent("API.Database.Redis.Subscriber")
    private RedisSubscriber     redisSubscriber;
    @InjectComponent("API.Database.Redis.Observer")
    private IObservationManager observationManager;
    @InjectComponent("API.MinecraftNetwork.PlayersStorage")
    private IPlayersData        playersData;
    private JedisPool           jedisPool;
    private Value<NetworkMeta>  networkMeta;

    @Override
    protected void enableComponent()
    {
        this.jedisPool = this.<StorageConnector>getComponent("API.Database.StorageConnector").getJedisPool();
        this.networkMeta = this.observationManager.get(NetworkMeta.class, "network:meta");
        this.redisSubscriber.subscribe(NETWORK_ACTION, this::handleNetworkAction);
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
        try (final Jedis jedis = this.jedisPool.getResource())
        {
            return jedis.keys(PROXY_INSTANCE + "*").stream().map(id -> this.msgPack.deserialize(ProxyInstanceInfo.class, jedis.get(id.getBytes()))).collect(Collectors.toSet());
        }
    }

    @Override
    public Set<RemoteDaemon> getDaemons()
    {
        try (final Jedis jedis = this.jedisPool.getResource())
        {
            return jedis.keys(DAEMON + "*").stream().map(id -> this.msgPack.deserialize(RemoteDaemon.class, jedis.get(id.getBytes()))).collect(Collectors.toSet());
        }
    }

    /**
     * Zwraca aktualną listę serwerów w tej sieci.
     *
     * @return lista serwerów.
     */
    @Override
    public Set<Server> getServers()
    {
        try (final Jedis jedis = this.jedisPool.getResource())
        {
            return jedis.keys(SERVER + "*").stream().map(id -> this.msgPack.deserialize(ServerImpl.class, jedis.get(id.getBytes()))).collect(Collectors.toSet());
        }
    }

    @Override
    public Set<Server> getServers(final String serversGroup)
    {
        return this.getServers().stream()
                   .filter(server -> server.getServersGroup().isPresent())
                   .filter(server -> server.getServersGroup().get().getName().equals(serversGroup))
                   .collect(Collectors.toSet());
    }

    /**
     * Zwraca aktualną listę grup serwerów skonfigurowanych w tej sieci.
     *
     * @return lista grup serwerów.
     */
    @Override
    public Set<IServersGroup> getServersGroups()
    {
        try (final Jedis jedis = this.jedisPool.getResource())
        {
            return Sets.newCopyOnWriteArraySet(this.msgPack.deserializeList(IServersGroup.class, jedis.get(NETWORK_SERVER_GROUPS.getBytes())));
        }
    }

    @Override
    public IServersGroup getServersGroup(final String name)
    {
        return this.getServersGroups().stream().filter(serversGroup -> serversGroup.getName().equals(name)).findAny().orElse(null);
    }

    /**
     * Zwraca aktualną listę minigier skonfigurowanych w tej sieci.
     *
     * @return lista mini gier.
     */
    @Override
    public List<MiniGame> getMiniGames()
    {
        try (final Jedis jedis = this.jedisPool.getResource())
        {
            return this.msgPack.deserializeList(MiniGame.class, jedis.get(NETWORK_MINIGAMES.getBytes()));
        }
    }

    /**
     * Zwraca konfigurację minigry o podanej nazwie systemowej.
     *
     * @param name nazwa systemowa minigry
     * @return konfiguracja minigry
     */
    @Override
    public MiniGame getMiniGame(final String name)
    {
        return this.getMiniGames().stream().filter(miniGame -> miniGame.getSystemName().equals(name)).findAny().orElse(null);
    }

    /**
     * Zwraca aktualną listę wzorów instancji serwerów skonfigurowanych w tej sieci.
     *
     * @return lista server patternów.
     */
    @Override
    public List<ServerPattern> getServerPatterns()
    {
        try (final Jedis jedis = this.jedisPool.getResource())
        {
            return this.msgPack.deserializeList(ServerPattern.class, jedis.get(NETWORK_PATTERNS.getBytes()));
        }
    }

    /**
     * Zwraca konfigurację tego wzoru serwera.
     *
     * @param name nazwa wzoru.
     * @return konfiguracja wzoru.
     */
    @Override
    public ServerPattern getServerPattern(final String name)
    {
        return this.getServerPatterns().stream().filter(serverPattern -> serverPattern.getPatternName().equals(name)).findAny().orElse(null);
    }

    /**
     * Zwraca obiekt przechowujący informacje o danym serwerze.
     *
     * @param uuid unikalny identyfikator serwera.
     * @return informacje o serwerze.
     */
    @Override
    public Value<Server> getServer(final UUID uuid)
    {
        //noinspection unchecked
        return (Value) this.observationManager.get(ServerImpl.class, SERVER + uuid);
    }

    @Override
    public int onlinePlayersCount()
    {
        return this.getProxyServers().stream().mapToInt(ProxyInstanceInfo::getOnlinePlayers).sum();
    }

    @Override
    public Value<IOnlinePlayer> getOnlinePlayer(final String nick)
    {
        //noinspection unchecked
        return (Value) this.observationManager.get(OnlinePlayerImpl.class, PLAYERS + nick.toLowerCase(Locale.ROOT));
    }

    @Override
    public Value<IOnlinePlayer> getOnlinePlayer(final UUID playerUuid)
    {
        return this.getOnlinePlayer(this.playersData.uuidToUsername(playerUuid));
    }

    @Override
    public IOfflinePlayer getOfflinePlayer(final UUID playerUuid)
    {
        return this.playersData.getOfflinePlayer(playerUuid);
    }

    @Override
    public IOfflinePlayer getOfflinePlayer(final String nick)
    {
        return this.playersData.getOfflinePlayer(nick);
    }

    @Override
    public void savePlayer(final IPlayer player)
    {
        this.playersData.savePlayer(player);
    }

    /**
     * Sprawdza czy gracz o danym nicku jest aktualnie na serwerze.
     * Nie jest uwzględniana wielkość liter.
     *
     * @param nick Nazwa gracza do sprawdzenia.
     * @return czy gracz jest online w sieci.
     */
    @Override
    public boolean isOnline(final String nick)
    {
        try (final Jedis jedis = this.jedisPool.getResource())
        {
            return jedis.exists(PLAYERS + nick.toLowerCase(Locale.ROOT));
        }
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
        try (final Jedis jedis = this.jedisPool.getResource())
        {
            jedis.publish(NETWORK_ACTION, networkAction.toString());
        }
    }

    /**
     * Zwraca instancję zdalnego wywoływania procedur do kontrolera sieci.
     *
     * @return NetworkControllerRpc
     */
    @Override
    public NetworkControllerRpc getNetworkController()
    {
        // todo add local RpcManager module
        return this.getApiCore().getRpcManager().createRpcProxy(NetworkControllerRpc.class, Targets.networkController());
    }

    private void handleNetworkAction(final String channel, final byte[] message)
    {
        final NetworkAction networkAction = NetworkAction.valueOf(SafeEncoder.encode(message));
        switch (networkAction)
        {
            case STOP_ALL:
                API.getPlatformConnector().stop();
                break;
            case KICK_ALL:
                API.getPlatformConnector().kickAll();
                break;
            case UPDATE_NETWORK_CONFIGS:
                this.getApiCore().getPermissionsManager().synchronizeGroups();
                break;
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
