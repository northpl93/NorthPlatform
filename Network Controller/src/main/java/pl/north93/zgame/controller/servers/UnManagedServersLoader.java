package pl.north93.zgame.controller.servers;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.impl.Injector;
import pl.north93.zgame.api.global.deployment.serversgroup.UnManagedServer;
import pl.north93.zgame.api.global.deployment.serversgroup.UnManagedServersGroup;
import pl.north93.zgame.api.global.network.JoiningPolicy;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.network.server.ServerImpl;
import pl.north93.zgame.api.global.network.server.ServerState;
import pl.north93.zgame.api.global.network.server.ServerType;
import pl.north93.zgame.api.global.redis.observable.IObservationManager;
import pl.north93.zgame.controller.configbroadcaster.ConfigBroadcaster;

/**
 * Klasa wczytująca serwery unmanaged z configu, tworzy obiekty
 * i wysyła do redisa.
 */
public class UnManagedServersLoader
{
    @InjectComponent("NetworkController.ConfigBroadcaster")
    private ConfigBroadcaster   configBroadcaster;
    @InjectComponent("API.Database.Redis.Observer")
    private IObservationManager observationManager;

    public UnManagedServersLoader()
    {
        Injector.inject(API.getApiCore().getComponentManager(), this);
    }

    public void broadcastServers()
    {
        for (final UnManagedServersGroup serversGroup : this.configBroadcaster.getServersGroups().getUnManagedGroups())
        {
            final ServerType serverType = serversGroup.getServersType();
            final JoiningPolicy joiningPolicy = serversGroup.getJoiningPolicy();

            for (final UnManagedServer unManaged : serversGroup.getServers())
            {
                final Server server = new ServerImpl(UUID.fromString(unManaged.getServerId()), false, serverType, ServerState.INSTALLING, joiningPolicy, unManaged.getConnectIp(), unManaged.getConnectPort(), serversGroup, null);
                this.observationManager.of(server); // uploads to redis
            }
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
