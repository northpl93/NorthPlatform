package pl.north93.zgame.restful.controllers;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.config.IConfig;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.JoiningPolicy;
import pl.north93.zgame.api.global.network.NetworkMeta;
import pl.north93.zgame.api.global.network.event.NetworkKickAllNetEvent;
import pl.north93.zgame.api.global.network.event.NetworkShutdownNetEvent;
import pl.north93.zgame.api.global.redis.event.IEventManager;
import pl.north93.zgame.restful.models.NetworkJoinPolicyChanged;
import pl.north93.zgame.restful.models.NetworkStatus;
import spark.Request;
import spark.Response;

public class NetworkController
{
    @Inject
    private IEventManager   eventManager;
    @Inject
    private INetworkManager networkManager;

    public Object root(final Request request, final Response response)
    {
        final NetworkMeta meta = this.networkManager.getNetworkConfig().get();
        final int onlinePlayers = this.networkManager.getProxies().onlinePlayersCount();

        return new NetworkStatus(meta.displayMaxPlayers, onlinePlayers, meta.joiningPolicy, meta.serverListMotd);
    }

    public Object joinpolicy(final Request request, final Response response)
    {
        final IConfig<NetworkMeta> networkConfig = this.networkManager.getNetworkConfig();
        final NetworkMeta networkMeta = networkConfig.get();

        final JoiningPolicy oldJoinPolicy = networkMeta.joiningPolicy;
        final JoiningPolicy newJoinPolicy = JoiningPolicy.valueOf(request.params(":policy"));

        networkMeta.joiningPolicy = newJoinPolicy;
        networkConfig.update(networkMeta);

        return new NetworkJoinPolicyChanged(oldJoinPolicy, newJoinPolicy);
    }

    public Object kickall(final Request request, final Response response)
    {
        this.eventManager.callEvent(new NetworkKickAllNetEvent());
        return "ok";
    }

    public Object stopall(final Request request, final Response response)
    {
        this.eventManager.callEvent(new NetworkShutdownNetEvent());
        return "ok";
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
