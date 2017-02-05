package pl.north93.zgame.controller.configbroadcaster;

import static pl.north93.zgame.api.global.cfg.ConfigUtils.loadConfigFile;
import static pl.north93.zgame.api.global.redis.RedisKeys.NETWORK_ACTION;
import static pl.north93.zgame.api.global.redis.RedisKeys.NETWORK_META;
import static pl.north93.zgame.api.global.redis.RedisKeys.NETWORK_MINIGAMES;
import static pl.north93.zgame.api.global.redis.RedisKeys.NETWORK_PATTERNS;
import static pl.north93.zgame.api.global.redis.RedisKeys.NETWORK_SERVER_GROUPS;
import static pl.north93.zgame.api.global.redis.RedisKeys.PERMISSIONS_GROUPS;


import java.util.ArrayList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.data.StorageConnector;
import pl.north93.zgame.api.global.deployment.ServerPattern;
import pl.north93.zgame.api.global.deployment.serversgroup.IServersGroup;
import pl.north93.zgame.api.global.messages.GroupsContainer;
import pl.north93.zgame.api.global.messages.NetworkMeta;
import pl.north93.zgame.api.global.network.NetworkAction;
import pl.north93.zgame.api.global.network.minigame.MiniGame;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.controller.cfg.MinigamesConfig;
import pl.north93.zgame.controller.cfg.ServersGroupsConfig;
import pl.north93.zgame.controller.cfg.ServersPatternsConfig;
import redis.clients.jedis.Jedis;

/**
 * Klasa zarządzająca wczytywaniem konfiguracji sieci i wysyłaniem jej do Redisa.
 */
public class ConfigBroadcaster extends Component
{
    @InjectComponent("API.Database.StorageConnector")
    private StorageConnector      storage;
    @InjectComponent("API.Database.Redis.MessagePackSerializer")
    private TemplateManager       msgPack;

    private NetworkMeta           networkMeta;
    private GroupsContainer       groups;
    private MinigamesConfig       miniGames;
    private ServersPatternsConfig patternsConfig;
    private ServersGroupsConfig   serversGroups;

    @Override
    protected void enableComponent()
    {
        this.loadAndBroadcastConfigs();
    }

    @Override
    protected void disableComponent()
    {
        try (final Jedis jedis = this.storage.getJedisPool().getResource())
        {
            jedis.del(NETWORK_META, PERMISSIONS_GROUPS, NETWORK_MINIGAMES, NETWORK_SERVER_GROUPS, NETWORK_PATTERNS);
        }
    }

    public NetworkMeta getNetworkMeta()
    {
        return this.networkMeta;
    }

    public MinigamesConfig getMiniGames()
    {
        return this.miniGames;
    }

    public ServersGroupsConfig getServersGroups()
    {
        return this.serversGroups;
    }

    public ServersPatternsConfig getPatternsConfig()
    {
        return this.patternsConfig;
    }

    public void loadAndBroadcastConfigs()
    {
        this.networkMeta = loadConfigFile(NetworkMeta.class, this.getApiCore().getFile("network.yml"));
        this.groups = loadConfigFile(GroupsContainer.class, this.getApiCore().getFile("permissions.yml"));
        this.miniGames = loadConfigFile(MinigamesConfig.class, this.getApiCore().getFile("minigames.yml"));
        this.patternsConfig = loadConfigFile(ServersPatternsConfig.class, this.getApiCore().getFile("instancepatterns.yml"));
        this.serversGroups = loadConfigFile(ServersGroupsConfig.class, this.getApiCore().getFile("serversgroups.yml"));
        this.broadcastNetworkConfigs();
    }

    private void broadcastNetworkConfigs()
    {
        try (final Jedis jedis = this.storage.getJedisPool().getResource())
        {
            jedis.set(NETWORK_META.getBytes(), this.msgPack.serialize(this.networkMeta));
            jedis.set(PERMISSIONS_GROUPS.getBytes(), this.msgPack.serialize(this.groups));
            jedis.set(NETWORK_MINIGAMES.getBytes(), this.msgPack.serializeList(MiniGame.class, this.miniGames.getMiniGames()));

            final ArrayList<IServersGroup> serversGroups = new ArrayList<>();
            serversGroups.addAll(this.serversGroups.getManagedGroups());
            serversGroups.addAll(this.serversGroups.getUnManagedGroups());
            jedis.set(NETWORK_SERVER_GROUPS.getBytes(), this.msgPack.serializeList(IServersGroup.class, serversGroups));

            jedis.set(NETWORK_PATTERNS.getBytes(), this.msgPack.serializeList(ServerPattern.class, this.patternsConfig.getPatterns()));

            jedis.publish(NETWORK_ACTION, NetworkAction.UPDATE_NETWORK_CONFIGS.toString());
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("networkMeta", this.networkMeta).append("groups", this.groups).append("miniGames", this.miniGames).append("patternsConfig", this.patternsConfig).append("serversGroups", this.serversGroups).toString();
    }
}
