package pl.north93.zgame.controller.servers.groups;

import pl.north93.zgame.api.global.network.JoiningPolicy;
import pl.north93.zgame.api.global.network.server.ServerType;
import pl.north93.zgame.api.global.network.server.group.ServersGroupDto;
import pl.north93.zgame.api.global.network.server.group.ServersGroupType;

abstract class AbstractLocalServersGroup implements ILocalServersGroup
{
    protected final ServersGroupDto dto;

    public AbstractLocalServersGroup(final ServersGroupDto dto)
    {
        this.dto = dto;
    }

    @Override
    public ServersGroupDto getAsDto()
    {
        return this.dto;
    }

    @Override
    public ServersGroupType getType()
    {
        return this.dto.getType();
    }

    @Override
    public String getName()
    {
        return this.dto.getName();
    }

    @Override
    public ServerType getServersType()
    {
        return this.dto.getServersType();
    }

    @Override
    public JoiningPolicy getJoiningPolicy()
    {
        return this.dto.getJoiningPolicy();
    }
}
