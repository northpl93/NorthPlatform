package pl.north93.zgame.controller.servers.scaler.value;

import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.controller.servers.groups.LocalManagedServersGroup;

public class PlayersCountValue implements IScalingValue
{
    @Inject
    private INetworkManager networkManager;

    @Override
    public String getId()
    {
        return "playersCount";
    }

    @Override
    public double calculate(final LocalManagedServersGroup managedServersGroup)
    {
        final Set<Server> servers = this.networkManager.getServers().inGroup(managedServersGroup.getName());
        return servers.stream().mapToInt(Server::getPlayersCount).sum();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
