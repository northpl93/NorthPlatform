package pl.north93.zgame.skyblock.server.actions;

import java.util.UUID;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.server.joinaction.IServerJoinAction;
import pl.north93.zgame.api.global.redis.messaging.annotations.MsgPackIgnore;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyblock.server.management.IslandHostManager;
import pl.north93.zgame.skyblock.shared.api.ServerMode;

public class TeleportPlayerToIsland implements IServerJoinAction
{
    @Inject
    @MsgPackIgnore
    private SkyBlockServer server;
    private UUID           islandId;

    public TeleportPlayerToIsland() // serialization
    {
    }

    public TeleportPlayerToIsland(final UUID islandId)
    {
        this.islandId = islandId;
    }

    @Override
    public void playerJoined(final Player bukkitPlayer)
    {
        if (this.server.getServerMode().equals(ServerMode.LOBBY))
        {
            return;
        }
        final IslandHostManager serverManager = this.server.getServerManager();
        bukkitPlayer.teleport(serverManager.getIsland(this.islandId).getHomeLocation());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("islandId", this.islandId).toString();
    }
}
