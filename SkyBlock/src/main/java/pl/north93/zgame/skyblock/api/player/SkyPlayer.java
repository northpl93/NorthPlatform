package pl.north93.zgame.skyblock.api.player;

import java.util.UUID;

import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.network.players.IOfflinePlayer;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.skyblock.api.IslandRole;

public abstract class SkyPlayer
{
    protected static final MetaKey PLAYER_HAS_ISLAND = MetaKey.get("sky:hasIsland");
    protected static final MetaKey PLAYER_ISLAND_ID  = MetaKey.get("sky:islandId");
    protected static final MetaKey PLAYER_ROLE       = MetaKey.get("sky:role");
    protected static final MetaKey PLAYER_ISLAND_COL = MetaKey.get("sky:islColdown");

    public abstract boolean hasIsland();

    public abstract UUID getIslandId();

    public abstract IslandRole getIslandRole();

    public abstract long getIslandCooldown();

    public abstract void setIslandCooldown(long cooldown);

    public abstract void setIsland(final UUID islandId, final IslandRole islandRole);

    public static SkyPlayer get(final Value<IOnlinePlayer> onlinePlayer)
    {
        return new OnlineSkyPlayer(onlinePlayer);
    }

    public static SkyPlayer get(final IOfflinePlayer offlinePlayer)
    {
        return new OfflineSkyPlayer(offlinePlayer);
    }
}
