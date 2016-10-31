package pl.north93.zgame.daemon.servers;

import java.io.IOException;

import pl.north93.zgame.api.global.API;

/**
 * Reprezentuje proces i konsolę serwera.
 */
public class ServerConsole
{
    private final ServerInstance serverInstance;
    private final Process        serverProcess;

    public ServerConsole(final ServerInstance serverInstance, final Process serverProcess)
    {
        this.serverInstance = serverInstance;
        this.serverProcess = serverProcess;
    }

    public ServerInstance getServerInstance()
    {
        return this.serverInstance;
    }

    public Process getServerProcess()
    {
        return this.serverProcess;
    }

    public static ServerConsole createServerProcess(final ServerInstance serverInstance)
    {
        final String javaStartLine = serverInstance.getJava().buildStartLine();
        final ProcessBuilder procBuilder = new ProcessBuilder(javaStartLine);
        procBuilder.directory(serverInstance.getWorkspace());

        final Process process;
        try
        {
            API.getLogger().info("Starting process: " + javaStartLine);
            process = procBuilder.start();
        }
        catch (final IOException e)
        {
            throw new RuntimeException("Failed to create server process.", e);
        }
        final ServerConsole console = new ServerConsole(serverInstance, process);
        serverInstance.setServerConsole(console);

        return console;
    }
}
