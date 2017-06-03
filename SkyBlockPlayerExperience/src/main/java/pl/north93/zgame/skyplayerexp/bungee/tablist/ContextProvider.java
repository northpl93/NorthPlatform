package pl.north93.zgame.skyplayerexp.bungee.tablist;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.skyblock.shared.api.IslandData;
import pl.north93.zgame.skyblock.shared.api.player.SkyPlayer;
import pl.north93.zgame.skyblock.bungee.SkyBlockBungee;

public final class ContextProvider
{
    @Inject
    private INetworkManager networkManager;
    @Inject
    private SkyBlockBungee  skyBlock;

    public TablistDrawingContext getFor(final ProxiedPlayer player)
    {
        final Value<IOnlinePlayer> netPlayer = this.networkManager.getOnlinePlayer(player.getName());
        final SkyPlayer skyPlayer = SkyPlayer.get(netPlayer);
        final IslandData islandData = skyPlayer.hasIsland() ? this.skyBlock.getIslandDao().getIsland(skyPlayer.getIslandId()) : null;
        return new TablistDrawingContext(player, netPlayer, islandData);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
