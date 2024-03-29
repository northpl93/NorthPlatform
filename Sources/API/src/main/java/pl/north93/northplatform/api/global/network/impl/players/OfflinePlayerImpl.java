package pl.north93.northplatform.api.global.network.impl.players;

import java.util.Objects;
import java.util.UUID;

import com.google.common.base.Preconditions;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.metadata.MetaStore;
import pl.north93.northplatform.api.global.network.players.IOfflinePlayer;
import pl.north93.northplatform.api.global.network.players.IOnlinePlayer;
import pl.north93.northplatform.api.global.permissions.Group;

public class OfflinePlayerImpl implements IOfflinePlayer
{
    private UUID      uuid;
    private Boolean   premium;
    private String    latestNick;
    private String    displayName;
    private Group     group;
    private Long      groupExpireAt;
    private MetaStore meta = new MetaStore();

    public OfflinePlayerImpl()
    {
    }

    public OfflinePlayerImpl(final UUID uuid, final Boolean premium, final String latestNick, final String displayName, final Group group, final Long groupExpireAt, final MetaStore meta)
    {
        this.uuid = uuid;
        this.premium = premium;
        this.latestNick = latestNick;
        this.displayName = displayName;
        this.group = group;
        this.groupExpireAt = groupExpireAt;
        this.meta = meta;
    }

    public OfflinePlayerImpl(final IOnlinePlayer onlinePlayer)
    {
        this.uuid = onlinePlayer.getUuid();
        this.premium = onlinePlayer.isPremium();
        this.latestNick = onlinePlayer.getLatestNick();
        if (onlinePlayer.hasDisplayName())
        {
            this.displayName = onlinePlayer.getDisplayName();
        }
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
    public boolean isPremium()
    {
        return this.premium;
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
        return this.latestNick;
    }

    @Override
    public void setDisplayName(final String newName)
    {
        this.displayName = newName;
    }

    @Override
    public Group getGroup()
    {
        return this.group;
    }

    @Override
    public long getGroupExpireAt()
    {
        return Objects.requireNonNullElse(this.groupExpireAt, 0L);
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
        return false;
    }

    @Override
    public MetaStore getOnlineMetaStore()
    {
        return new MetaStore();
    }

    @Override
    public IOnlinePlayer asOnlinePlayer()
    {
        return null;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("uuid", this.uuid).append("latestNick", this.latestNick).append("displayName", this.displayName).append("group", this.group).append("groupExpireAt", this.groupExpireAt).append("meta", this.meta).toString();
    }
}
