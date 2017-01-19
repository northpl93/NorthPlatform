package pl.north93.zgame.skyblock.api.player;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.metadata.MetaStore;
import pl.north93.zgame.api.global.network.players.IOfflinePlayer;
import pl.north93.zgame.skyblock.api.IslandRole;

class OfflineSkyPlayer extends SkyPlayer
{
    private final IOfflinePlayer offlinePlayer;

    public OfflineSkyPlayer(final IOfflinePlayer offlinePlayer)
    {
        this.offlinePlayer = offlinePlayer;
    }

    @Override
    public boolean hasIsland()
    {
        final MetaStore metaStore = this.offlinePlayer.getMetaStore();
        return metaStore.contains(PLAYER_HAS_ISLAND) && metaStore.getBoolean(PLAYER_HAS_ISLAND);
    }

    @Override
    public UUID getIslandId()
    {
        final MetaStore metaStore = this.offlinePlayer.getMetaStore();
        if (! metaStore.contains(PLAYER_ISLAND_ID))
        {
            return null;
        }
        return metaStore.getUuid(PLAYER_ISLAND_ID);
    }

    @Override
    public IslandRole getIslandRole()
    {
        final MetaStore metaStore = this.offlinePlayer.getMetaStore();
        if (! metaStore.contains(PLAYER_ROLE))
        {
            return null;
        }
        return IslandRole.values()[metaStore.getInteger(PLAYER_ROLE)];
    }

    @Override
    public void setIsland(final UUID islandId, final IslandRole islandRole)
    {
        final MetaStore metaStore = this.offlinePlayer.getMetaStore();
        if (islandId == null)
        {
            metaStore.setBoolean(PLAYER_HAS_ISLAND, false);
            metaStore.remove(PLAYER_ISLAND_ID);
            metaStore.remove(PLAYER_ROLE);
        }
        else
        {
            metaStore.setBoolean(PLAYER_HAS_ISLAND, true);
            metaStore.setUuid(PLAYER_ISLAND_ID, islandId);
            metaStore.setInteger(PLAYER_ROLE, islandRole.ordinal());
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("offlinePlayer", this.offlinePlayer).toString();
    }
}
