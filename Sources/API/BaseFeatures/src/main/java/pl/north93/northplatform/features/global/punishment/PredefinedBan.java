package pl.north93.northplatform.features.global.punishment;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import pl.north93.northplatform.api.global.metadata.MetaKey;
import pl.north93.northplatform.api.global.metadata.MetaStore;

public class PredefinedBan extends AbstractBan
{
    public static final MetaKey BAN_REASON = MetaKey.get("ban_reason");

    private final int banReason;

    public PredefinedBan(final UUID adminId, final Instant givenAt, final Duration duration, final int banReason)
    {
        super(adminId, givenAt, duration);
        this.banReason = banReason;
    }

    public PredefinedBan(final MetaStore store)
    {
        super(store);
        this.banReason = store.get(BAN_REASON);
    }

    @Override
    public void save(final MetaStore store)
    {
        super.save(store);
        store.set(BAN_TYPE, "predefined");
        store.set(BAN_REASON, this.banReason);
    }

    public int getBanReason()
    {
        return this.banReason;
    }
}
