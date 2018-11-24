package pl.north93.northplatform.controller.servers.groups;

import com.google.common.base.Preconditions;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.network.JoiningPolicy;
import pl.north93.northplatform.api.global.network.server.ServerType;
import pl.north93.northplatform.api.global.network.server.group.ServersGroupDto;
import pl.north93.northplatform.api.global.network.server.group.ServersGroupType;
import pl.north93.northplatform.api.global.network.daemon.config.ServersGroupConfig;

abstract class AbstractLocalServersGroup<CONFIG extends ServersGroupConfig> implements ILocalServersGroup
{
    protected final ServersGroupDto dto;
    protected final CONFIG          config;

    public AbstractLocalServersGroup(final ServersGroupDto dto, final CONFIG config)
    {
        this.dto = dto;
        this.config = config;
    }

    @Override
    public ServersGroupDto getAsDto()
    {
        return this.dto;
    }

    public CONFIG getConfig()
    {
        return this.config;
    }

    @Override
    public void mergeConfig(final ServersGroupConfig config)
    {
        Preconditions.checkState(this.config.getName().equals(config.getName()));
        Preconditions.checkState(this.config.getType() == config.getType());

        this.config.mergeConfigIntoThis(config);
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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("dto", this.dto).append("config", this.config).toString();
    }
}
