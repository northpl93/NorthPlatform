package pl.north93.zgame.api.global.network.impl;

import static pl.north93.zgame.api.global.redis.RedisKeys.PLAYERS;


import java.util.Locale;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.metadata.MetaStore;
import pl.north93.zgame.api.global.network.ProxyRpc;
import pl.north93.zgame.api.global.network.players.IOfflinePlayer;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.network.server.ServerProxyData;
import pl.north93.zgame.api.global.network.server.joinaction.IServerJoinAction;
import pl.north93.zgame.api.global.network.server.joinaction.JoinActionsContainer;
import pl.north93.zgame.api.global.permissions.Group;
import pl.north93.zgame.api.global.redis.observable.ObjectKey;
import pl.north93.zgame.api.global.redis.rpc.Targets;

/**
 * Reprezentuje gracza będącego online w sieci
 */
public class OnlinePlayerImpl implements IOnlinePlayer
{
    private UUID      uuid;
    private String    nick;
    private String    latestNick;
    private UUID      serverId;
    private String    proxyId;
    private Boolean   premium;
    private Boolean   isBanned;
    private Group     group;
    private Long      groupExpireAt;
    private MetaStore meta = new MetaStore();

    @Override
    public ObjectKey getKey()
    {
        return new ObjectKey(PLAYERS + this.getNick().toLowerCase(Locale.ROOT));
    }

    @Override
    public void transferDataFrom(final IOfflinePlayer offlinePlayer)
    {
        this.latestNick = offlinePlayer.getLatestNick();
        this.isBanned = offlinePlayer.isBanned();
        this.group = offlinePlayer.getGroup();
        this.groupExpireAt = offlinePlayer.getGroupExpireAt();
        this.meta = offlinePlayer.getMetaStore();
    }

    @Override
    public String getNick()
    {
        return this.nick;
    }

    public void setNick(final String nick)
    {
        this.nick = nick;
    }

    @Override
    public String getLatestNick()
    {
        return this.latestNick;
    }

    @Override
    public boolean isBanned()
    {
        if (this.isBanned)
        {
            return false;
        }
        return this.isBanned;
    }

    @Override
    public void setBanned(final boolean banned)
    {
        this.isBanned = banned;
    }

    public void setLatestNick(final String latestNick)
    {
        this.latestNick = latestNick;
    }

    @Override
    public UUID getUuid()
    {
        return this.uuid;
    }

    public void setUuid(final UUID uuid)
    {
        this.uuid = uuid;
    }

    @Override
    public String getProxyId()
    {
        return this.proxyId;
    }

    public void setProxyId(final String proxyId)
    {
        this.proxyId = proxyId;
    }

    @Override
    public UUID getServerId()
    {
        return this.serverId;
    }

    @Override
    public void setServerId(final UUID serverId)
    {
        this.serverId = serverId;
    }

    @Override
    public boolean isPremium()
    {
        return this.premium;
    }

    public void setPremium(final boolean premium)
    {
        this.premium = premium;
    }

    @Override
    public Group getGroup()
    {
        return this.group;
    }

    @Override
    public long getGroupExpireAt()
    {
        if (this.groupExpireAt == null)
        {
            return 0;
        }
        return this.groupExpireAt;
    }

    @Override
    public void setGroup(final Group group)
    {
        this.group = group;
    }

    @Override
    public void setGroupExpireAt(final long expireAt)
    {
        this.groupExpireAt = expireAt;
    }

    @Override
    public boolean isOnline()
    {
        return this.getProxyRpc().isOnline(this.nick);
    }

    public boolean hasPermission(final String permission)
    {
        return this.group != null && this.group.hasPermission(permission);
    }

    @Override
    public MetaStore getMetaStore()
    {
        return this.meta;
    }

    /**
     * Wysyła wiadomość do gracza.
     * @param message treść wiadomości.
     */
    @Override
    public void sendMessage(final String message)
    {
        this.getProxyRpc().sendMessage(this.nick, message);
    }

    /**
     * Wyrzuca gracza z serwera.
     * @param message wiadomość która pokaże się po wyrzuceniu.
     */
    @Override
    public void kick(final String message)
    {
        this.getProxyRpc().kick(this.nick, message);
    }

    @Override
    public void connectTo(final ServerProxyData server, IServerJoinAction... actions)
    {
        this.getProxyRpc().connectPlayer(this.nick, server.getProxyName(), new JoinActionsContainer(actions));
    }

    @Override
    public void connectTo(final String serversGroupName, IServerJoinAction... actions)
    {
        this.getProxyRpc().connectPlayerToServersGroup(this.nick, serversGroupName, new JoinActionsContainer(actions));
    }

    private ProxyRpc getProxyRpc()
    {
        return API.getRpcManager().createRpcProxy(ProxyRpc.class, Targets.proxy(this.proxyId));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("uuid", this.uuid).append("nick", this.nick).append("latestNick", this.latestNick).append("serverId", this.serverId).append("proxyId", this.proxyId).append("premium", this.premium).append("group", this.group).append("groupExpireAt", this.groupExpireAt).append("meta", this.meta).toString();
    }
}
