package pl.arieals.api.minigame.server.lobby;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.IServerManager;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;

public class LobbyManager implements IServerManager
{
    @Inject
    private INetworkManager networkManager;
    @Inject
    private IRpcManager     rpcManager;

    @Override
    public void start()
    {
    }

    @Override
    public void stop()
    {
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
