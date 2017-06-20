package pl.arieals.api.minigame.server.lobby;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.IServerManager;
import pl.arieals.api.minigame.server.lobby.arenas.IArenaClient;
import pl.arieals.api.minigame.server.lobby.arenas.impl.ArenaClientImpl;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;

public class LobbyManager implements IServerManager
{
    @Inject
    private INetworkManager networkManager;
    @Inject
    private IRpcManager     rpcManager;
    private IArenaClient    arenaClient;

    @Override
    public void start()
    {
        this.arenaClient = new ArenaClientImpl();
    }

    @Override
    public void stop()
    {
    }

    public IArenaClient getArenaClient()
    {
        return this.arenaClient;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
