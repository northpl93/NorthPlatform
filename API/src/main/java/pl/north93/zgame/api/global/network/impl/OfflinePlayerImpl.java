package pl.north93.zgame.api.global.network.impl;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.metadata.MetaStore;
import pl.north93.zgame.api.global.network.players.IOfflinePlayer;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.permissions.Group;

public class OfflinePlayerImpl implements IOfflinePlayer
{
    private UUID    uuid;
    private String  latestNick;
    private Boolean isBanned;
    private Group   group;
    private Long    groupExpireAt;
    private MetaStore meta = new MetaStore();

    public OfflinePlayerImpl()
    {
    }

    public OfflinePlayerImpl(final UUID uuid, final String latestNick, final Boolean isBanned, final Group group, final Long groupExpireAt, final MetaStore meta)
    {
        this.uuid = uuid;
        this.latestNick = latestNick;
        this.isBanned = isBanned;
        this.group = group;
        this.groupExpireAt = groupExpireAt;
        this.meta = meta;
    }

    public OfflinePlayerImpl(final IOnlinePlayer onlinePlayer)
    {
        this.uuid = onlinePlayer.getUuid();
        this.latestNick = onlinePlayer.getLatestNick();
        this.isBanned = onlinePlayer.isBanned();
        this.group = onlinePlayer.getGroup();
        this.groupExpireAt = onlinePlayer.getGroupExpireAt();
        this.meta = onlinePlayer.getMetaStore();
    }

    @Override
    public MetaStore getMetaStore()
    {
        return this.meta;
    }

    @Override
    public UUID getUuid()
    {
        return this.uuid;
    }

    @Override
    public String getLatestNick()
    {
        return this.latestNick;
    }

    @Override
    public boolean isBanned()
    {
        if (this.isBanned == null)
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
        return false;
    }

    @Override
    public IOnlinePlayer asOnlinePlayer()
    {
        return null;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("uuid", this.uuid).append("latestNick", this.latestNick).append("group", this.group).append("meta", this.meta).toString();
    }
}
