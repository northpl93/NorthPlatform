package pl.north93.zgame.skyblock.shared.api.player;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.metadata.MetaStore;
import pl.north93.zgame.api.global.network.players.IPlayer;
import pl.north93.zgame.skyblock.shared.api.IslandRole;

public class NewApiSkyPlayer extends SkyPlayer
{
    private final IPlayer player;

    public NewApiSkyPlayer(final IPlayer player)
    {
        this.player = player;
    }

    @Override
    public boolean hasIsland()
    {
        final MetaStore metaStore = this.player.getMetaStore();
        return metaStore.contains(PLAYER_HAS_ISLAND) && metaStore.getBoolean(PLAYER_HAS_ISLAND);
    }

    @Override
    public UUID getIslandId()
    {
        final MetaStore metaStore = this.player.getMetaStore();
        if (! metaStore.contains(PLAYER_ISLAND_ID))
        {
            return null;
        }
        return metaStore.getUuid(PLAYER_ISLAND_ID);
    }

    @Override
    public IslandRole getIslandRole()
    {
        final MetaStore metaStore = this.player.getMetaStore();
        if (! metaStore.contains(PLAYER_ROLE))
        {
            return null;
        }
        return IslandRole.values()[metaStore.getInteger(PLAYER_ROLE)];
    }

    @Override
    public long getIslandCooldown()
    {
        final MetaStore metaStore = this.player.getMetaStore();
        if (! metaStore.contains(PLAYER_ISLAND_COL))
        {
            return 0;
        }
        return metaStore.getLong(PLAYER_ISLAND_COL);
    }

    @Override
    public void setIslandCooldown(final long cooldown)
    {
        this.player.getMetaStore().setLong(PLAYER_ISLAND_COL, cooldown);
    }

    @Override
    public void setIsland(final UUID islandId, final IslandRole islandRole)
    {
        final MetaStore metaStore = this.player.getMetaStore();
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("player", this.player).toString();
    }
}
