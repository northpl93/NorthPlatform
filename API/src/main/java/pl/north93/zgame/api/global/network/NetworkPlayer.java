package pl.north93.zgame.api.global.network;

import static pl.north93.zgame.api.global.redis.RedisKeys.PLAYERS;


import java.util.Locale;
import java.util.UUID;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.permissions.Group;
import pl.north93.zgame.api.global.redis.messaging.RedisUpdatable;
import pl.north93.zgame.api.global.redis.messaging.annotations.MsgPackCustomTemplate;
import pl.north93.zgame.api.global.redis.messaging.templates.extra.GroupInStringTemplate;
import pl.north93.zgame.api.global.redis.rpc.Targets;

public class NetworkPlayer implements RedisUpdatable
{
    private String  nick;
    private UUID    uuid;
    private String  proxyId;
    private String  server;
    private Boolean premium;
    @MsgPackCustomTemplate(GroupInStringTemplate.class)
    private Group   group;

    @Override
    public String getRedisKey()
    {
        return PLAYERS + this.getNick().toLowerCase(Locale.ROOT);
    }

    public String getNick()
    {
        return this.nick;
    }

    public void setNick(final String nick)
    {
        this.nick = nick;
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

    public String getServer()
    {
        return this.server;
    }

    public void setServer(final String server)
    {
        this.server = server;
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

    /**
     * Wysyła wiadomość do gracza.
     * @param message treść wiadomości.
     */
    public void sendMessage(final String message)
    {
        API.getRpcManager().createRpcProxy(ProxyRpc.class, Targets.proxy(this.proxyId)).sendMessage(this.nick, message);
        //API.getPacketManager().sendPacket(Targets.proxy(this.proxyId), new PacketProxyBoundSendMessage(this.nick, message));
    }

    /**
     * Wyrzuca gracza z serwera.
     * @param message wiadomość która pokaże się po wyrzuceniu.
     */
    public void kick(final String message)
    {
        API.getRpcManager().createRpcProxy(ProxyRpc.class, Targets.proxy(this.proxyId)).kick(this.nick, message);
        //API.getPacketManager().sendPacket(Targets.proxy(this.proxyId), new PacketProxyBoundKick(this.nick, message));
    }
}
