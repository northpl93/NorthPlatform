package pl.north93.zgame.daemon.servers;

import javax.annotation.Nullable;

import java.io.File;
import java.util.UUID;

import pl.north93.zgame.api.global.network.impl.ServerDto;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.api.global.utils.JavaArguments;

/**
 * Reprezentuje instancje serwera.
 */
public class LocalServerInstance
{
    private final Value<ServerDto>   serverDto;
    private final File               workspace;
    private final JavaArguments      arguments;
    private final LocalServerConsole console;

    public LocalServerInstance(final Value<ServerDto> serverDto, final File workspace, final JavaArguments arguments)
    {
        this.serverDto = serverDto;
        this.workspace = workspace;
        this.arguments = arguments;
        this.console = LocalServerConsole.createProcess(this);
    }

    public Value<ServerDto> getServerDto()
    {
        return this.serverDto;
    }

    public @Nullable UUID getServerId()
    {
        return this.serverDto.getOptional().map(ServerDto::getUuid).orElse(null);
    }

    public File getWorkspace()
    {
        return this.workspace;
    }

    public JavaArguments getArguments()
    {
        return this.arguments;
    }

    public LocalServerConsole getConsole()
    {
        return this.console;
    }

    /**
     * Sprawdza czy proces serwera zostal zatrzymany.
     *
     * @return True jesli proces serwera nie jest uruchomiony.
     */
    public boolean isStopped()
    {
        return ! this.console.getProcess().isAlive();
    }
}
