package pl.north93.zgame.features.global.punishment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.metadata.MetaStore;

public abstract class AbstractBan
{
    public static final MetaKey BAN_TYPE     = MetaKey.get("ban_type");
    public static final MetaKey BAN_ADMIN_ID = MetaKey.get("ban_adminId");
    public static final MetaKey BAN_GIVEN_AT = MetaKey.get("ban_givenAt");
    public static final MetaKey BAN_DURATION = MetaKey.get("ban_duration");

    private final UUID     adminId;
    private final Instant  givenAt;
    private final Duration duration;

    public AbstractBan(final UUID adminId, final Instant givenAt, final Duration duration)
    {
        this.adminId = adminId;
        this.givenAt = givenAt;
        this.duration = duration;
    }

    public AbstractBan(final MetaStore store)
    {
        this.adminId = store.get(BAN_ADMIN_ID);
        this.givenAt = store.getInstant(BAN_GIVEN_AT);
        this.duration = store.getDuration(BAN_DURATION);
    }

    public void save(final MetaStore store)
    {
        store.set(BAN_ADMIN_ID, this.adminId);
        store.setInstant(BAN_GIVEN_AT, this.givenAt);
        if (this.duration == null)
        {
            store.remove(BAN_DURATION);
        }
        else
        {
            store.setDuration(BAN_DURATION, this.duration);
        }
    }

    @Nullable
    public UUID getAdminId()
    {
        return this.adminId;
    }

    @Nonnull
    public Instant getGivenAt()
    {
        return this.givenAt;
    }

    @Nullable
    public Duration getDuration()
    {
        return this.duration;
    }

    public boolean isExpired()
    {
        if (this.duration == null)
        {
            return false;
        }

        final Instant expireDate = this.givenAt.plus(this.duration);
        return expireDate.isBefore(Instant.now());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("adminId", this.adminId).append("givenAt", this.givenAt).append("duration", this.duration).toString();
    }
}
