package pl.north93.zgame.controller.servers.groups;

import java.util.UUID;

import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.JoiningPolicy;
import pl.north93.zgame.api.global.network.impl.ServerDto;
import pl.north93.zgame.api.global.network.server.ServerState;
import pl.north93.zgame.api.global.network.server.ServerType;
import pl.north93.zgame.api.global.network.server.group.ServersGroupDto;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.controller.servers.cfg.UnManagedServer;
import pl.north93.zgame.controller.servers.cfg.UnManagedServersGroupConfig;

public class LocalUnManagedServersGroup extends AbstractLocalServersGroup<UnManagedServersGroupConfig>
{
    @Inject
    private INetworkManager networkManager;

    public LocalUnManagedServersGroup(final ServersGroupDto dto, final UnManagedServersGroupConfig config)
    {
        super(dto, config);
    }

    private void insertServer(final ServerDto serverDto)
    {
        final Value<ServerDto> value = this.networkManager.getServers().unsafe().getServerDto(serverDto.getUuid());
        value.set(serverDto);
    }

    @Override
    public void init()
    {
        final ServersGroupDto groupDto = this.getAsDto();
        final ServerType serversType = this.config.getServersType();
        final JoiningPolicy joiningPolicy = this.config.getJoiningPolicy();

        for (final UnManagedServer serverConfig : this.config.getServers())
        {
            final UUID serverId = serverConfig.getServerId();
            final String connectIp = serverConfig.getConnectIp();
            final Integer connectPort = serverConfig.getConnectPort();

            final ServerDto serverDto = new ServerDto(serverId, false, serversType, ServerState.CREATING, joiningPolicy, connectIp, connectPort, groupDto);
            this.insertServer(serverDto);
        }
    }
}
