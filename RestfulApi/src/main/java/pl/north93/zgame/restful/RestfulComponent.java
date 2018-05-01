package pl.north93.zgame.restful;

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.stop;


import com.google.gson.Gson;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.restful.controllers.NetworkController;
import pl.north93.zgame.restful.controllers.PlayerController;
import pl.north93.zgame.restful.controllers.ServersController;
import pl.north93.zgame.restful.security.ApiSecurity;

public class RestfulComponent extends Component
{
    private final Gson gson = new Gson();

    @Override
    protected void enableComponent()
    {
        before((request, response) -> response.type("application/json;charset=utf-8"));

        final PlayerController player = new PlayerController();
        get("player/:nick", player::root, this.gson::toJson);

        final NetworkController network = new NetworkController();
        get("network", network::root, this.gson::toJson);
        get("network/joinpolicy/:policy", network::joinpolicy, this.gson::toJson);
        get("network/action/kickall", network::kickall);
        get("network/action/stopall", network::stopall);

        final ServersController servers = new ServersController();
        get("servers", servers::root, this.gson::toJson);
        get("servers/:uuid", servers::getServer, this.gson::toJson);

        final ApiSecurity apiSecurity = new ApiSecurity();
        apiSecurity.setupSecurity();
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
