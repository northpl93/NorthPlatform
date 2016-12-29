package pl.north93.zgame.skyblock.api;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.metadata.MetaStore;
import pl.north93.zgame.api.global.network.NetworkPlayer;
import pl.north93.zgame.api.global.redis.observable.Value;

public final class SkyPlayer
{
    private static final MetaKey PLAYER_HAS_ISLAND = MetaKey.get("sky:hasIsland");
    private static final MetaKey PLAYER_ISLAND_ID  = MetaKey.get("sky:islandId");
    private final Value<NetworkPlayer> networkPlayer;

    public SkyPlayer(final Value<NetworkPlayer> networkPlayer)
    {
        this.networkPlayer = networkPlayer;
    }

    public Value<NetworkPlayer> getNetworkPlayer()
    {
        return this.networkPlayer;
    }

    public boolean hasIsland()
    {
        final MetaStore metaStore = this.networkPlayer.get().getMetaStore();
        return metaStore.contains(PLAYER_HAS_ISLAND) && metaStore.getBoolean(PLAYER_HAS_ISLAND);
    }

    public UUID getIslandId()
    {
        final MetaStore metaStore = this.networkPlayer.get().getMetaStore();
        if (! metaStore.contains(PLAYER_ISLAND_ID))
        {
            return null;
        }
        return metaStore.getUuid(PLAYER_ISLAND_ID);
    }

    public void setIsland(final UUID islandId)
    {
        final MetaStore metaStore = this.networkPlayer.get().getMetaStore();
        if (islandId == null)
        {
            metaStore.setBoolean(PLAYER_HAS_ISLAND, false);
            metaStore.remove(PLAYER_ISLAND_ID);
        }
        else
        {
            metaStore.setBoolean(PLAYER_HAS_ISLAND, true);
            metaStore.setUuid(PLAYER_ISLAND_ID, islandId);
        }
        this.networkPlayer.upload();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("networkPlayer", this.networkPlayer).toString();
    }
}
