package pl.north93.northplatform.controller.servers.groups;

import pl.north93.northplatform.api.global.network.daemon.config.ManagedServersGroupConfig;
import pl.north93.northplatform.api.global.network.daemon.config.ServersGroupConfig;
import pl.north93.northplatform.api.global.network.daemon.config.UnManagedServersGroupConfig;
import pl.north93.northplatform.api.global.network.server.group.ServersGroupDto;

public class LocalServersGroupFactory
{
    public static final LocalServersGroupFactory INSTANCE = new LocalServersGroupFactory();

    public ILocalServersGroup createLocalGroup(final ServersGroupConfig config)
    {
        final ServersGroupDto serversGroupDto = new ServersGroupDto(config.getName(), config.getType(), config.getServersType(), config.getJoiningPolicy());

        switch (config.getType())
        {
            case MANAGED:
                return new LocalManagedServersGroup(serversGroupDto, (ManagedServersGroupConfig) config);
            case UN_MANAGED:
                return new LocalUnManagedServersGroup(serversGroupDto, (UnManagedServersGroupConfig) config);
            default:
                throw new IllegalArgumentException(config.toString());
        }
    }
}
