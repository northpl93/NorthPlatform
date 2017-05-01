package pl.north93.zgame.restful.controllers;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.network.NetworkMeta;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.JoiningPolicy;
import pl.north93.zgame.api.global.network.NetworkAction;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.restful.models.NetworkJoinPolicyChanged;
import pl.north93.zgame.restful.models.NetworkStatus;
import spark.Request;
import spark.Response;

public class NetworkController
{
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager networkManager;

    public Object root(final Request request, final Response response)
    {
        final NetworkMeta meta = this.networkManager.getNetworkMeta().get();
        final int onlinePlayers = this.networkManager.getPlayers().onlinePlayersCount();

        return new NetworkStatus(meta.displayMaxPlayers, onlinePlayers, meta.joiningPolicy, meta.serverListMotd);
    }

    public Object joinpolicy(final Request request, final Response response)
    {
        final Value<NetworkMeta> networkMeta = this.networkManager.getNetworkMeta();

        final JoiningPolicy oldJoinPolicy = networkMeta.get().joiningPolicy;
        final JoiningPolicy newJoinPolicy = JoiningPolicy.valueOf(request.params(":policy"));

        networkMeta.update(meta ->
        {
            meta.joiningPolicy = newJoinPolicy;
        });

        return new NetworkJoinPolicyChanged(oldJoinPolicy, newJoinPolicy);
    }

    public Object kickall(final Request request, final Response response)
    {
        this.networkManager.broadcastNetworkAction(NetworkAction.KICK_ALL);
        return "ok";
    }

    public Object stopall(final Request request, final Response response)
    {
        this.networkManager.broadcastNetworkAction(NetworkAction.STOP_ALL);
        return "ok";
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
