package pl.north93.zgame.restful.controllers;

import static java.util.Optional.ofNullable;

import static spark.Spark.halt;


import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.deployment.ServerPattern;
import pl.north93.zgame.api.global.deployment.serversgroup.IServersGroup;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.restful.models.ServerModel;
import spark.Request;
import spark.Response;

public class ServersController
{
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager networkManager;

    public Object root(final Request request, final Response response)
    {
        return this.networkManager.getServers().stream().map(this::serverToModel).collect(Collectors.toList());
    }

    public Object getServer(final Request request, final Response response)
    {
        final UUID serverId = UUID.fromString(request.params(":uuid"));
        final Value<Server> server = this.networkManager.getServer(serverId);
        if (! server.isAvailable())
        {
            halt(404);
        }
        return this.serverToModel(server.get());
    }

    private ServerModel serverToModel(final Server s)
    {
        final String type = s.getType().name();
        final boolean isDaemon = s.isLaunchedViaDaemon();
        final String state = s.getServerState().name();
        final String joinPolicy = s.getJoiningPolicy().name();
        final String pattern = ofNullable(s.getServerPattern()).map(ServerPattern::getPatternName).orElse("");
        final String group = s.getServersGroup().map(IServersGroup::getName).orElse("");
        return new ServerModel(s.getUuid(), type, isDaemon, state, joinPolicy, pattern, group);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}