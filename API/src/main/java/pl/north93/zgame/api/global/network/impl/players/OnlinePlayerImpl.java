package pl.north93.zgame.api.global.network.impl.players;

import static pl.north93.zgame.api.global.redis.RedisKeys.PLAYERS;


import java.util.Locale;
import java.util.UUID;

import com.google.common.base.Preconditions;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import pl.north93.zgame.api.bukkit.utils.chat.ChatUtils;
import pl.north93.zgame.api.global.messages.MessageLayout;
import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.metadata.MetaStore;
import pl.north93.zgame.api.global.network.players.IOfflinePlayer;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.network.proxy.IProxyRpc;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.network.server.joinaction.IServerJoinAction;
import pl.north93.zgame.api.global.permissions.Group;
import pl.north93.zgame.api.global.permissions.GroupInStringTemplate;
import pl.north93.zgame.api.global.redis.observable.ObjectKey;
import pl.north93.zgame.api.global.serializer.platform.annotations.NorthCustomTemplate;

/**
 * Reprezentuje gracza będącego online w sieci
 */
public class OnlinePlayerImpl implements IOnlinePlayer
{
    private UUID      uuid;
    private String    nick;
    private String    latestNick;
    private String    displayName;
    private UUID      serverId;
    private String    proxyId;
    private Boolean   premium;
    @NorthCustomTemplate(GroupInStringTemplate.class)
    private Group     group;
    private Long      groupExpireAt;
    private MetaStore meta = new MetaStore();
    private MetaStore onlineMeta = new MetaStore();

    @Override
    public ObjectKey getKey()
    {
        return new ObjectKey(PLAYERS + this.getNick().toLowerCase(Locale.ROOT));
    }

    @Override
    public void transferDataFrom(final IOfflinePlayer offlinePlayer)
    {
        this.latestNick = offlinePlayer.getLatestNick();
        if (offlinePlayer.hasDisplayName())
        {
            this.displayName = offlinePlayer.getDisplayName();
        }
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
    public boolean hasDisplayName()
    {
        return this.displayName != null;
    }

    @Override
    public String getDisplayName()
    {
        if (this.hasDisplayName())
        {
            return this.displayName;
        }
        return this.nick;
    }

    @Override
    public void setDisplayName(final String newName)
    {
        this.displayName = newName;
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
        Preconditions.checkNotNull(group, "Group can't be null");
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
        final IProxyRpc proxyRpc = PlayerHelper.INSTANCE.getProxyRpc(this);
        return proxyRpc.isOnline(this.nick);
    }

    @Override
    public MetaStore getMetaStore()
    {
        return this.meta;
    }

    @Override
    public MetaStore getOnlineMetaStore()
    {
        return this.onlineMeta;
    }

    @Override
    public Locale getMyLocale()
    {
        final MetaKey metaKey = MetaKey.get("lang");
        if (this.meta.contains(metaKey))
        {
            return Locale.forLanguageTag(this.meta.get(metaKey));
        }
        return Locale.forLanguageTag("pl-PL");
    }

    /**
     * Wysyła wiadomość do gracza.
     * @param message treść wiadomości.
     */
    @Override
    public void sendMessage(final String message, final MessageLayout layout)
    {
        final BaseComponent component = layout.processMessage(ChatUtils.fromLegacyText(message));

        final IProxyRpc proxyRpc = PlayerHelper.INSTANCE.getProxyRpc(this);
        proxyRpc.sendJsonMessage(this.nick, ComponentSerializer.toString(component));
    }

    @Override
    public void sendMessage(final BaseComponent component, final MessageLayout layout)
    {
        final String serialized = ComponentSerializer.toString(layout.processMessage(component));

        final IProxyRpc proxyRpc = PlayerHelper.INSTANCE.getProxyRpc(this);
        proxyRpc.sendJsonMessage(this.nick, serialized);
    }

    /**
     * Wyrzuca gracza z serwera.
     * @param message wiadomość która pokaże się po wyrzuceniu.
     */
    @Override
    public void kick(final BaseComponent message)
    {
        final String serialized = ComponentSerializer.toString(message);

        final IProxyRpc proxyRpc = PlayerHelper.INSTANCE.getProxyRpc(this);
        proxyRpc.kick(this.nick, serialized);
    }

    @Override
    public void connectTo(final Server server, final IServerJoinAction... actions)
    {
        PlayerHelper.INSTANCE.connectTo(this, server, actions);
    }

    @Override
    public void connectTo(final String serversGroupName, final IServerJoinAction... actions)
    {
        PlayerHelper.INSTANCE.connectTo(this, serversGroupName, actions);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("uuid", this.uuid).append("nick", this.nick).append("latestNick", this.latestNick).append("displayName", this.displayName).append("serverId", this.serverId).append("proxyId", this.proxyId).append("premium", this.premium).append("group", this.group).append("groupExpireAt", this.groupExpireAt).append("meta", this.meta).toString();
    }
}
