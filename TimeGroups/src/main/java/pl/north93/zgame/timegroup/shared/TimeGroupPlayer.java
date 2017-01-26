package pl.north93.zgame.timegroup.shared;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.metadata.MetaStore;
import pl.north93.zgame.api.global.network.players.IPlayer;

public class TimeGroupPlayer
{
    private static final MetaKey KEY_EXPIRE   = MetaKey.get("timegroups:expireAt");
    private static final MetaKey KEY_GIVEN_AT = MetaKey.get("timegroups:expireAt");
    private static final MetaKey KEY_GROUP    = MetaKey.get("timegroups:group");
    private final IPlayer player;

    public TimeGroupPlayer(final IPlayer player)
    {
        this.player = player;
    }

    public boolean hasGroup()
    {
        return this.player.getMetaStore().contains(KEY_GROUP);
    }

    public String getGroup()
    {
        return this.player.getMetaStore().getString(KEY_GROUP);
    }

    public long getExpireTime()
    {
        return this.player.getMetaStore().getLong(KEY_EXPIRE);
    }

    public void setGroup(final String group, final long expireAt)
    {
        final MetaStore metaStore = this.player.getMetaStore();
        metaStore.setLong(KEY_GIVEN_AT, System.currentTimeMillis());
        metaStore.setLong(KEY_EXPIRE, expireAt);
        metaStore.setString(KEY_GROUP, group);
    }

    public void removeGroup()
    {
        final MetaStore metaStore = this.player.getMetaStore();
        metaStore.remove(KEY_GIVEN_AT);
        metaStore.remove(KEY_GROUP);
        metaStore.remove(KEY_EXPIRE);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("player", this.player).toString();
    }
}
