package pl.north93.northplatform.controller.servers.scaler.value;

import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.server.IServersManager;
import pl.north93.northplatform.api.global.network.server.Server;
import pl.north93.northplatform.controller.servers.groups.LocalManagedServersGroup;

public class ServersCountValue implements IScalingValue
{
    @Inject
    private IServersManager serversManager;

    @Override
    public String getId()
    {
        return "serversCount";
    }

    @Override
    public double calculate(final LocalManagedServersGroup managedServersGroup)
    {
        final Set<Server> servers = this.serversManager.inGroup(managedServersGroup.getName());
        return servers.size();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
