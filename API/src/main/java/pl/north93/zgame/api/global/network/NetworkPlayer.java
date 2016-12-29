package pl.north93.zgame.api.global.network;

import static pl.north93.zgame.api.global.redis.RedisKeys.PLAYERS;


import java.util.Locale;
import java.util.UUID;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.metadata.MetaStore;
import pl.north93.zgame.api.global.metadata.Metadatable;
import pl.north93.zgame.api.global.network.server.ServerProxyData;
import pl.north93.zgame.api.global.permissions.Group;
import pl.north93.zgame.api.global.redis.observable.ObjectKey;
import pl.north93.zgame.api.global.redis.observable.ProvidingRedisKey;
import pl.north93.zgame.api.global.redis.rpc.Targets;
import pl.north93.zgame.api.global.utils.Messageable;

/**
 * Reprezentuje gracza będącego online w sieci
 */
public class NetworkPlayer implements Messageable, Metadatable, ProvidingRedisKey
{
    private UUID      uuid;
    private String    nick;
    private String    latestNick;
    private UUID      serverId;
    private String    proxyId;
    private Boolean   premium;
    private Group     group;
    private MetaStore meta = new MetaStore();

    @Override
    public ObjectKey getKey()
    {
        return new ObjectKey(PLAYERS + this.getNick().toLowerCase(Locale.ROOT));
    }

    public String getNick()
    {
        return this.nick;
    }

    public void setNick(final String nick)
    {
        this.nick = nick;
    }

    public String getLatestNick()
    {
        return this.latestNick;
    }

    public void setLatestNick(final String latestNick)
    {
        this.latestNick = latestNick;
    }

    public UUID getUuid()
    {
        return this.uuid;
    }

    public void setUuid(final UUID uuid)
    {
        this.uuid = uuid;
    }

    public String getProxyId()
    {
        return this.proxyId;
    }

    public void setProxyId(final String proxyId)
    {
        this.proxyId = proxyId;
    }

    public UUID getServerId()
    {
        return this.serverId;
    }

    public void setServerId(final UUID serverId)
    {
        this.serverId = serverId;
    }

    public boolean isPremium()
    {
        return this.premium;
    }

    public void setPremium(final boolean premium)
    {
        this.premium = premium;
    }

    public Group getGroup()
    {
        return this.group;
    }

    public void setGroup(final Group group)
    {
        this.group = group;
    }

    public boolean hasPermission(final String permission)
    {
        return this.group != null && this.group.getPermissions().contains(permission);
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
    public void kick(final String message)
    {
        this.getProxyRpc().kick(this.nick, message);
    }

    public void connectTo(final ServerProxyData data)
    {
        this.getProxyRpc().connectPlayer(this.nick, data.getProxyName());
    }

    public void connectTo(final String serversGroupName)
    {

    }

    private ProxyRpc getProxyRpc()
    {
        return API.getRpcManager().createRpcProxy(ProxyRpc.class, Targets.proxy(this.proxyId));
    }
}
