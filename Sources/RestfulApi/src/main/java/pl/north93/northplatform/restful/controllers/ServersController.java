package pl.north93.northplatform.restful.controllers;

import static spark.Spark.halt;


import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.server.IServersManager;
import pl.north93.northplatform.api.global.network.server.Server;
import pl.north93.northplatform.restful.models.ServerModel;
import spark.Request;
import spark.Response;

public class ServersController
{
    @Inject
    private IServersManager serversManager;

    public Object root(final Request request, final Response response)
    {
        return this.serversManager.all().stream().map(this::serverToModel).collect(Collectors.toList());
    }

    public Object getServer(final Request request, final Response response)
    {
        final UUID serverId = UUID.fromString(request.params(":uuid"));
        final Server server = this.serversManager.withUuid(serverId);
        if (server == null)
        {
            halt(404);
        }
        return this.serverToModel(server);
    }

    private ServerModel serverToModel(final Server s)
    {
        final String type = s.getType().name();
        final boolean isDaemon = s.isLaunchedViaDaemon();
        final String state = s.getServerState().name();
        final String joinPolicy = s.getJoiningPolicy().name();
        final String group = s.getServersGroup().getName();
        return new ServerModel(s.getUuid(), type, isDaemon, state, joinPolicy, group);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
