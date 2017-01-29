package pl.north93.zgame.restful;

import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.stop;


import com.google.gson.Gson;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.messages.NetworkMeta;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.utils.Wrapper;
import pl.north93.zgame.restful.models.NetworkStatus;
import pl.north93.zgame.restful.models.PlayerModel;

public class RestfulComponent extends Component
{
    private final Gson gson = new Gson();
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager networkManager;

    @Override
    protected void enableComponent()
    {
        get("player/:nick", (request, response) ->
        {
            final Wrapper<Object> myResponse = new Wrapper<>();
            this.networkManager.getPlayers().access(request.params(":nick"), online ->
            {
                myResponse.set(new PlayerModel(online.getUuid(), online.getNick(), true, online.getGroup().getName(), online.getMetaStore()));
            }, offline ->
            {
                myResponse.set(new PlayerModel(offline.getUuid(), offline.getLatestNick(), false, offline.getGroup().getName(), offline.getMetaStore()));
            });

            if (myResponse.get() == null)
            {
                halt(404);
            }
            return myResponse.get();
        }, this.gson::toJson);

        get("network", (request, response) ->
        {
            final NetworkMeta meta = this.networkManager.getNetworkMeta().get();
            final int onlinePlayers = this.networkManager.getPlayers().onlinePlayersCount();

            return new NetworkStatus(meta.displayMaxPlayers, onlinePlayers, meta.joiningPolicy, meta.serverListMotd);
        }, this.gson::toJson);
    }

    @Override
    protected void disableComponent()
    {
        stop(); // stop webserver
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
