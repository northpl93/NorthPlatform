package pl.north93.zgame.api.global.network;

import static pl.north93.zgame.api.global.redis.RedisKeys.DAEMON;
import static pl.north93.zgame.api.global.redis.RedisKeys.NETWORK_ACTION;
import static pl.north93.zgame.api.global.redis.RedisKeys.NETWORK_META;
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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.deployment.RemoteDaemon;
import pl.north93.zgame.api.global.deployment.ServerPattern;
import pl.north93.zgame.api.global.deployment.ServersGroup;
import pl.north93.zgame.api.global.messages.NetworkMeta;
import pl.north93.zgame.api.global.messages.ProxyInstanceInfo;
import pl.north93.zgame.api.global.network.minigame.MiniGame;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.redis.rpc.Targets;
import pl.north93.zgame.api.global.utils.ObservableValue;
import redis.clients.jedis.Jedis;
import redis.clients.util.SafeEncoder;

public class NetworkManager
{
    private final ApiCore api = API.getApiCore();
    private final ObservableValue<NetworkMeta> networkMeta = new ObservableValue<>();

    public void start()
    {
        this.downloadNetworkMeta(); // Download network meta on start
        this.api.getRedisSubscriber().subscribe(NETWORK_ACTION, this::handleNetworkAction);
    }

    public ObservableValue<NetworkMeta> getNetworkMeta()
    {
        return this.networkMeta;
    }

    public JoiningPolicy getJoiningPolicy()
    {
        return this.networkMeta.get().joiningPolicy;
    }

    /**
     * Zwraca aktualną listę serwerów proxy podłączonych do sieci.
     *
     * @return lista serwerów proxy.
     */
    public Set<ProxyInstanceInfo> getProxyServers()
    {
        try (final Jedis jedis = API.getJedis().getResource())
        {
            return jedis.keys(PROXY_INSTANCE + "*").stream().map(id -> this.api.getMessagePackTemplates().deserialize(ProxyInstanceInfo.class, jedis.get(id.getBytes()))).collect(Collectors.toSet());
        }
    }

    /**
     * Zwraca aktualną listę demonów podłączonych do sieci.
     *
     * @return lista demonów.
     */
    public Set<RemoteDaemon> getDaemons()
    {
        try (final Jedis jedis = API.getJedis().getResource())
        {
            return jedis.keys(DAEMON + "*").stream().map(id -> this.api.getMessagePackTemplates().deserialize(RemoteDaemon.class, jedis.get(id.getBytes()))).collect(Collectors.toSet());
        }
    }

    /**
     * Zwraca aktualną listę grup serwerów skonfigurowanych w tej sieci.
     *
     * @return lista grup serwerów.
     */
    public List<ServersGroup> getServersGroups()
    {
        try (final Jedis jedis = API.getJedis().getResource())
        {
            return API.getMessagePackTemplates().deserializeList(ServersGroup.class, jedis.get(NETWORK_SERVER_GROUPS.getBytes()));
        }
    }

    public ServersGroup getServersGroup(final String name)
    {
        return this.getServersGroups().stream().filter(serversGroup -> serversGroup.getName().equals(name)).findAny().orElse(null);
    }

    /**
     * Zwraca aktualną listę minigier skonfigurowanych w tej sieci.
     *
     * @return lista mini gier.
     */
    public List<MiniGame> getMiniGames()
    {
        try (final Jedis jedis = API.getJedis().getResource())
        {
            return API.getMessagePackTemplates().deserializeList(MiniGame.class, jedis.get(NETWORK_MINIGAMES.getBytes()));
        }
    }

    /**
     * Zwraca konfigurację minigry o podanej nazwie systemowej.
     *
     * @param name nazwa systemowa minigry
     * @return konfiguracja minigry
     */
    public MiniGame getMiniGame(final String name)
    {
        return this.getMiniGames().stream().filter(miniGame -> miniGame.getSystemName().equals(name)).findAny().orElse(null);
    }

    /**
     * Zwraca aktualną listę wzorów instancji serwerów skonfigurowanych w tej sieci.
     *
     * @return lista server patternów.
     */
    public List<ServerPattern> getServerPatterns()
    {
        try (final Jedis jedis = API.getJedis().getResource())
        {
            return API.getMessagePackTemplates().deserializeList(ServerPattern.class, jedis.get(NETWORK_PATTERNS.getBytes()));
        }
    }

    /**
     * Zwraca konfigurację tego wzoru serwera.
     *
     * @param name nazwa wzoru.
     * @return konfiguracja wzoru.
     */
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
    public Server getServer(final UUID uuid)
    {
        try (final Jedis jedis = API.getJedis().getResource())
        {
            final byte[] serverData = jedis.get((SERVER + uuid).getBytes());
            if (serverData == null)
            {
                return null;
            }
            return API.getMessagePackTemplates().deserialize(Server.class, serverData);
        }
    }

    public int onlinePlayersCount()
    {
        return this.getProxyServers().stream().mapToInt(ProxyInstanceInfo::getOnlinePlayers).sum();
    }

    public NetworkPlayer getNetworkPlayer(final String nick)
    {
        try (final Jedis jedis = API.getJedis().getResource())
        {
            final byte[] networkPlayerData = jedis.get((PLAYERS + nick.toLowerCase(Locale.ROOT)).getBytes());
            if (networkPlayerData == null)
            {
                return null;
            }
            return API.getMessagePackTemplates().deserialize(NetworkPlayer.class, networkPlayerData);
        }
    }

    /**
     * Sprawdza czy gracz o danym nicku jest aktualnie na serwerze.
     * Nie jest uwzględniana wielkość liter.
     *
     * @param nick Nazwa gracza do sprawdzenia.
     * @return czy gracz jest online w sieci.
     */
    public boolean isOnline(final String nick)
    {
        try (final Jedis jedis = API.getJedis().getResource())
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
    public void broadcastNetworkAction(final NetworkAction networkAction)
    {
        try (final Jedis jedis = API.getJedis().getResource())
        {
            jedis.publish(NETWORK_ACTION, networkAction.toString());
        }
    }

    /**
     * Zwraca instancję zdalnego wywoływania procedur do kontrolera sieci.
     *
     * @return NetworkControllerRpc
     */
    public NetworkControllerRpc getNetworkController()
    {
        return this.api.getRpcManager().createRpcProxy(NetworkControllerRpc.class, Targets.networkController());
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
                this.downloadNetworkMeta();
                this.api.getPermissionsManager().synchronizeGroups();
                break;
        }
    }

    private void downloadNetworkMeta()
    {
        this.api.getLogger().info("Downloading network info.");
        try (final Jedis jedis = this.api.getJedis().getResource())
        {
            if (! jedis.exists(NETWORK_META))
            {
                this.api.getLogger().warning("Can't download network info. Key doesn't exists.");
                return;
            }

            this.networkMeta.set(this.api.getMessagePackTemplates().deserialize(NetworkMeta.class, jedis.get(NETWORK_META.getBytes())));
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("api", this.api).toString();
    }
}
