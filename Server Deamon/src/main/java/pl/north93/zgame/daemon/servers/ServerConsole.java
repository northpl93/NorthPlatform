package pl.north93.zgame.daemon.servers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.API;

/**
 * Reprezentuje proces i konsolę serwera.
 */
public class ServerConsole
{
    private final ServerInstance      serverInstance;
    private final Process             serverProcess;
    private final InputStream         inputStream;
    private final ReadableByteChannel inputChannel;
    private final Logger              logger;

    public ServerConsole(final ServerInstance serverInstance, final Process serverProcess, final Logger parentLogger)
    {
        this.serverInstance = serverInstance;
        this.serverProcess = serverProcess;
        this.inputStream = serverProcess.getInputStream();
        this.inputChannel = Channels.newChannel(this.inputStream);
        this.logger = Logger.getLogger("Server-" + serverInstance.getServerId());
        this.logger.setParent(parentLogger);
    }

    public ServerInstance getServerInstance()
    {
        return this.serverInstance;
    }

    public Process getServerProcess()
    {
        return this.serverProcess;
    }

    public void executeCommand(final String consoleCommand)
    {
        try
        {
            final OutputStream outputStream = this.serverProcess.getOutputStream();
            outputStream.write(consoleCommand.getBytes());
            outputStream.write("\n".getBytes());
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }

    /*default*/ boolean doReadCycle() throws Exception // wykonuje sie w petli
    {
        if (this.inputStream.available() < 2)
        {
            return false;
        }

        final ByteBuffer allocate = ByteBuffer.allocate(1000);
        final int read = this.inputChannel.read(allocate);
        final byte[] bytes = allocate.array();
        final byte[] result = new byte[read];
        System.arraycopy(bytes, 0, result, 0, read);
        this.logger.info(new String(result).trim());
        return true;
    }

    /*default*/ void serverProcessStopped() // wykonywane gdy proces serwera sie wylaczy
    {
        API.getLogger().info("Server process with ID " + this.serverInstance.getServerId() + " stopped!");
        this.serverInstance.setServerConsole(null); // usuniecie konsoli serwera z instancji
        this.serverInstance.getServersManager().removeServer(this.serverInstance.getServerId()); // usuniecie serwera z demona i sieci
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("serverInstance", this.serverInstance).append("serverProcess", this.serverProcess).toString();
    }

    public static ServerConsole createServerProcess(final ServersManager serversManager, final ServerInstance serverInstance)
    {
        final String javaStartLine = serverInstance.getJava().buildStartLine();
        final ProcessBuilder procBuilder = new ProcessBuilder(StringUtils.split(javaStartLine, ' '));
        procBuilder.directory(serverInstance.getWorkspace());

        final Process process;
        try
        {
            API.getLogger().info("Starting process for server with UUID: " + serverInstance.getServerId());
            process = procBuilder.start();
        }
        catch (final IOException e)
        {
            throw new RuntimeException("Failed to create server process.", e);
        }
        final ServerConsole console = new ServerConsole(serverInstance, process, serversManager.getServersLogger());
        serverInstance.setServerConsole(console);
        serversManager.getWatchdog().addConsole(console);

        return console;
    }
}
