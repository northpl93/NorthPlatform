package pl.north93.zgame.controller.configbroadcaster;

import static pl.north93.zgame.api.global.cfg.ConfigUtils.loadConfigFile;
import static pl.north93.zgame.api.global.redis.RedisKeys.NETWORK_ACTION;
import static pl.north93.zgame.api.global.redis.RedisKeys.NETWORK_META;
import static pl.north93.zgame.api.global.redis.RedisKeys.NETWORK_MINIGAMES;
import static pl.north93.zgame.api.global.redis.RedisKeys.NETWORK_PATTERNS;
import static pl.north93.zgame.api.global.redis.RedisKeys.NETWORK_SERVER_GROUPS;
import static pl.north93.zgame.api.global.redis.RedisKeys.PERMISSIONS_GROUPS;
import static pl.north93.zgame.api.global.utils.StringUtils.toBytes;


import java.util.ArrayList;

import com.lambdaworks.redis.api.sync.RedisCommands;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.data.StorageConnector;
import pl.north93.zgame.api.global.deployment.ServerPattern;
import pl.north93.zgame.api.global.deployment.serversgroup.IServersGroup;
import pl.north93.zgame.api.global.network.NetworkAction;
import pl.north93.zgame.api.global.network.NetworkMeta;
import pl.north93.zgame.api.global.permissions.GroupsContainer;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.controller.cfg.ServersGroupsConfig;
import pl.north93.zgame.controller.cfg.ServersPatternsConfig;

/**
 * Klasa zarządzająca wczytywaniem konfiguracji sieci i wysyłaniem jej do Redisa.
 */
public class ConfigBroadcaster extends Component
{
    @Inject
    private StorageConnector      storage;
    @Inject
    private TemplateManager       msgPack;

    private NetworkMeta           networkMeta;
    private GroupsContainer       groups;
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
        try (final RedisCommands<String, byte[]> redis = this.storage.getRedis())
        {
            redis.del(NETWORK_META, PERMISSIONS_GROUPS, NETWORK_MINIGAMES, NETWORK_SERVER_GROUPS, NETWORK_PATTERNS);
        }
    }

    public NetworkMeta getNetworkMeta()
    {
        return this.networkMeta;
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
        this.patternsConfig = loadConfigFile(ServersPatternsConfig.class, this.getApiCore().getFile("instancepatterns.yml"));
        this.serversGroups = loadConfigFile(ServersGroupsConfig.class, this.getApiCore().getFile("serversgroups.yml"));
        this.broadcastNetworkConfigs();
    }

    private void broadcastNetworkConfigs()
    {
        try (final RedisCommands<String, byte[]> redis = this.storage.getRedis())
        {
            redis.set(NETWORK_META, this.msgPack.serialize(this.networkMeta));
            redis.set(PERMISSIONS_GROUPS, this.msgPack.serialize(this.groups));

            final ArrayList<IServersGroup> serversGroups = new ArrayList<>();
            serversGroups.addAll(this.serversGroups.getManagedGroups());
            serversGroups.addAll(this.serversGroups.getUnManagedGroups());
            redis.set(NETWORK_SERVER_GROUPS, this.msgPack.serializeList(IServersGroup.class, serversGroups));

            redis.set(NETWORK_PATTERNS, this.msgPack.serializeList(ServerPattern.class, this.patternsConfig.getPatterns()));

            redis.publish(NETWORK_ACTION, toBytes(NetworkAction.UPDATE_NETWORK_CONFIGS.toString()));
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("networkMeta", this.networkMeta).append("groups", this.groups).append("patternsConfig", this.patternsConfig).append("serversGroups", this.serversGroups).toString();
    }
}
