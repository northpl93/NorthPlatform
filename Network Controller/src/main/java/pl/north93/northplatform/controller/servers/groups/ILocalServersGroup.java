package pl.north93.northplatform.controller.servers.groups;

import pl.north93.northplatform.api.global.network.daemon.config.ServersGroupConfig;
import pl.north93.northplatform.api.global.network.server.group.IServersGroup;
import pl.north93.northplatform.api.global.network.server.group.ServersGroupDto;

public interface ILocalServersGroup extends IServersGroup
{
    ServersGroupDto getAsDto();

    void init();

    void mergeConfig(ServersGroupConfig config);
}
