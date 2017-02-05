package pl.north93.zgame.skyplayerexp.bungee.tablist;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.skyblock.api.IslandData;

public class TablistDrawingContext
{
    private final ProxiedPlayer        player;
    private final Value<IOnlinePlayer> networkPlayer;
    private final IslandData           islandData;

    public TablistDrawingContext(final ProxiedPlayer player, final Value<IOnlinePlayer> networkPlayer, final IslandData islandData)
    {
        this.player = player;
        this.networkPlayer = networkPlayer;
        this.islandData = islandData;
    }

    public ProxiedPlayer getPlayer()
    {
        return this.player;
    }

    public Value<IOnlinePlayer> getNetworkPlayer()
    {
        return this.networkPlayer;
    }

    public boolean hasIsland()
    {
        return this.islandData != null;
    }

    public IslandData getIslandData()
    {
        return this.islandData;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("player", this.player).append("networkPlayer", this.networkPlayer).append("islandData", this.islandData).toString();
    }
}
