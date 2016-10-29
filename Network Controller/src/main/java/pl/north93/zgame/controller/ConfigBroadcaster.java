package pl.north93.zgame.controller;

import static pl.north93.zgame.api.global.cfg.ConfigUtils.loadConfigFile;
import static pl.north93.zgame.api.global.redis.RedisKeys.NETWORK_ACTION;
import static pl.north93.zgame.api.global.redis.RedisKeys.NETWORK_META;
import static pl.north93.zgame.api.global.redis.RedisKeys.NETWORK_MINIGAMES;
import static pl.north93.zgame.api.global.redis.RedisKeys.NETWORK_PATTERNS;
import static pl.north93.zgame.api.global.redis.RedisKeys.NETWORK_SERVER_GROUPS;
import static pl.north93.zgame.api.global.redis.RedisKeys.PERMISSIONS_GROUPS;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.deployment.ServerPattern;
import pl.north93.zgame.api.global.deployment.ServersGroup;
import pl.north93.zgame.api.global.messages.GroupsContainer;
import pl.north93.zgame.api.global.messages.NetworkMeta;
import pl.north93.zgame.api.global.network.NetworkAction;
import pl.north93.zgame.api.global.network.minigame.MiniGame;
import pl.north93.zgame.controller.cfg.MinigamesConfig;
import pl.north93.zgame.controller.cfg.ServersGroupsConfig;
import pl.north93.zgame.controller.cfg.ServersPatternsConfig;
import redis.clients.jedis.Jedis;

/**
 * Klasa zarządzająca wczytywaniem konfiguracji sieci i wysyłaniem jej do Redisa.
 */
public class ConfigBroadcaster
{
    private NetworkMeta           networkMeta;
    private GroupsContainer       groups;
    private MinigamesConfig       miniGames;
    private ServersPatternsConfig patternsConfig;
    private ServersGroupsConfig   serversGroups;

    public void start()
    {
        this.networkMeta = loadConfigFile(NetworkMeta.class, API.getFile("network.yml"));
        this.groups = loadConfigFile(GroupsContainer.class, API.getFile("permissions.yml"));
        this.miniGames = loadConfigFile(MinigamesConfig.class, API.getFile("minigames.yml"));
        this.patternsConfig = loadConfigFile(ServersPatternsConfig.class, API.getFile("instancepatterns.yml"));
        this.serversGroups = loadConfigFile(ServersGroupsConfig.class, API.getFile("serversgroups.yml"));
        this.broadcastNetworkConfigs();
    }

    public void stop()
    {
        try (final Jedis jedis = API.getJedis().getResource())
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

    public void broadcastNetworkConfigs()
    {
        try (final Jedis jedis = API.getJedis().getResource())
        {
            jedis.set(NETWORK_META.getBytes(), API.getMessagePackTemplates().serialize(this.networkMeta));
            jedis.set(PERMISSIONS_GROUPS.getBytes(), API.getMessagePackTemplates().serialize(this.groups));
            jedis.set(NETWORK_MINIGAMES.getBytes(), API.getMessagePackTemplates().serializeList(MiniGame.class, this.miniGames.getMiniGames()));
            jedis.set(NETWORK_SERVER_GROUPS.getBytes(), API.getMessagePackTemplates().serializeList(ServersGroup.class, this.serversGroups.getGroups()));
            jedis.set(NETWORK_PATTERNS.getBytes(), API.getMessagePackTemplates().serializeList(ServerPattern.class, this.patternsConfig.getPatterns()));

            jedis.publish(NETWORK_ACTION, NetworkAction.UPDATE_NETWORK_CONFIGS.toString());
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("networkMeta", this.networkMeta).append("groups", this.groups).append("miniGames", this.miniGames).append("patternsConfig", this.patternsConfig).append("serversGroups", this.serversGroups).toString();
    }
}
