package pl.north93.zgame.skyblock.manager.servers;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.skyblock.shared.api.IIslandHostManager;

public class IslandHostServer
{
    private final UUID               uuid;
    private final IIslandHostManager islandHostManager;
    private final Value<Server>      serverValue;

    /*default*/ IslandHostServer(final UUID uuid, final IIslandHostManager islandHostManager, final Value<Server> serverValue)
    {
        this.uuid = uuid;
        this.islandHostManager = islandHostManager;
        this.serverValue = serverValue;
    }

    public UUID getUuid()
    {
        return this.uuid;
    }

    public IIslandHostManager getIslandHostManager()
    {
        return this.islandHostManager;
    }

    public Value<Server> getServerValue()
    {
        return this.serverValue;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("uuid", this.uuid).toString();
    }
}
