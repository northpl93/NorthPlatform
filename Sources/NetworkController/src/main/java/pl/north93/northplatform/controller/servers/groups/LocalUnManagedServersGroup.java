package pl.north93.northplatform.controller.servers.groups;

import java.util.UUID;

import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.JoiningPolicy;
import pl.north93.northplatform.api.global.network.daemon.config.ServersGroupConfig;
import pl.north93.northplatform.api.global.network.daemon.config.UnManagedServer;
import pl.north93.northplatform.api.global.network.daemon.config.UnManagedServersGroupConfig;
import pl.north93.northplatform.api.global.network.impl.servers.ServerDto;
import pl.north93.northplatform.api.global.network.server.IServersManager;
import pl.north93.northplatform.api.global.network.server.ServerState;
import pl.north93.northplatform.api.global.network.server.ServerType;
import pl.north93.northplatform.api.global.network.server.group.ServersGroupDto;
import pl.north93.northplatform.api.global.redis.observable.Value;

public class LocalUnManagedServersGroup extends AbstractLocalServersGroup<UnManagedServersGroupConfig>
{
    @Inject
    private IServersManager serversManager;

    public LocalUnManagedServersGroup(final ServersGroupDto dto, final UnManagedServersGroupConfig config)
    {
        super(dto, config);
    }

    @Override
    public void init()
    {
        for (final UnManagedServer serverConfig : this.config.getServers())
        {
            this.insertServer(serverConfig);
        }
    }

    @Override
    public void mergeConfig(final ServersGroupConfig config)
    {
        super.mergeConfig(config);
        final UnManagedServersGroupConfig updatedConfig = this.getConfig();

        for (final UnManagedServer unManagedServer : updatedConfig.getServers())
        {
            if (this.getServerValue(unManagedServer.getServerId()).isAvailable())
            {
                // serwer jest juz utworzony w redisie
                continue;
            }

            this.insertServer(unManagedServer);
        }
    }

    // tworzy nowa instancje obiektu serwera na podstawie configu i wrzuca ja do redisa
    private void insertServer(final UnManagedServer unManagedServer)
    {
        final ServersGroupDto groupDto = this.getAsDto();
        final ServerType serversType = this.config.getServersType();
        final JoiningPolicy joiningPolicy = this.config.getJoiningPolicy();

        final UUID serverId = unManagedServer.getServerId();
        final String connectIp = unManagedServer.getConnectIp();
        final Integer connectPort = unManagedServer.getConnectPort();

        final ServerDto serverDto = new ServerDto(serverId, false, serversType, ServerState.CREATING, joiningPolicy, connectIp, connectPort, groupDto);

        final Value<ServerDto> value = this.getServerValue(serverDto.getUuid());
        value.set(serverDto);
    }

    private Value<ServerDto> getServerValue(final UUID serverId)
    {
        return this.serversManager.unsafe().getServerDto(serverId);
    }
}
