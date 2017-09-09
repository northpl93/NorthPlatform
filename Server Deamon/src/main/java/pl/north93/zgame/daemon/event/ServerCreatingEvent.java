package pl.north93.zgame.daemon.event;

import java.io.File;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.network.daemon.config.ServerPatternConfig;
import pl.north93.zgame.api.global.network.impl.ServerDto;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.api.global.utils.JavaArguments;

public class ServerCreatingEvent
{
    private final File workspace;
    private final ServerPatternConfig pattern;
    private final JavaArguments arguments;
    private final Value<ServerDto> serverDtoValue;

    public ServerCreatingEvent(final File workspace, final ServerPatternConfig pattern, final JavaArguments arguments, final Value<ServerDto> serverDtoValue)
    {
        this.workspace = workspace;
        this.pattern = pattern;
        this.arguments = arguments;
        this.serverDtoValue = serverDtoValue;
    }

    public File getWorkspace()
    {
        return this.workspace;
    }

    public ServerPatternConfig getPattern()
    {
        return this.pattern;
    }

    public JavaArguments getArguments()
    {
        return this.arguments;
    }

    public Value<ServerDto> getServerDtoValue()
    {
        return this.serverDtoValue;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("workspace", this.workspace).append("pattern", this.pattern).append("arguments", this.arguments).append("serverDtoValue", this.serverDtoValue).toString();
    }
}
